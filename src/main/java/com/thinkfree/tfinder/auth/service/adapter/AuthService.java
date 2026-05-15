package com.thinkfree.tfinder.auth.service.adapter;

import com.thinkfree.tfinder.auth.service.dto.*;
import com.thinkfree.tfinder.auth.service.iface.IAuthUseCase;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IEmailValidateRepository;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IRefreshTokenRepository;
import com.thinkfree.tfinder.common.config.JwtProperties;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import com.thinkfree.tfinder.common.infrastructure.external.iface.IMailSender;
import com.thinkfree.tfinder.workspace.event.JoinPendingEvent;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthUseCase {

    private final PasswordEncoder encoder;
    private final IMemberRepository memberRepository;
    private final IJwtManager jwtManager;
    private final IRefreshTokenRepository refreshTokenRepository;
    private final IEmailValidateRepository emailValidateRepository;
    private final IMailSender mailSender;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtProperties jwtProperties;

    @Value("${frontend.url}")
    private String FRONTEND_URL;

    @Override
    public void requestEmailValidation(String email) {
        if (memberRepository.existsByEmail(email))
            throw new BusinessException(ErrorCode.DUPLICATE_ERROR);

        String token = jwtManager.generateValidateEmailToken(email);
        log.info("email validate request token = {}", token);

        // 한 사람에 대한 이메일 인증 요청이 여러번 가도 되는가?
        // SMTP 요청이 실패했는지 성공했는지 가져오질 못하니, 실패할 경우를 알 수 없다
        // 네트워크나 SMTP 서버 요청으로 인해 장애가 나서 인증정보만 대기중이고 메일이 가지 않았을 때
        // 중복 방지 로직으로 인해 인증 요청이 더이상 가지 못하면 회원이 가입을 할수가 없다.
        mailSender.asyncSend(
                email,
                "이메일 인증 요청",
                makeValidateMailMessage(token)
        );
        // 이메일 전송에서 실패하고 예외가 나면 어떻게 되지?
        // 1. 인증 요청을 해서 PENDING 상태의 정보가 저장됨 -> 근데 이 상태의 정보가 굳이 필요한가?
        // 토큰을 통해서 인증을 하고, 이 상태관리를 서버에서 할 필요가 있나?
        // 결론 : 굳이 PENDING 정보를 관리할 필요는 없다.
    }

    @Override
    public String emailValidate(String token) throws BusinessException {
        String email = jwtManager.getEmailFromValidateEmailToken(token);

        emailValidateRepository.saveAsValidated(email, getEmailExpiration()); // validate 상태 저장, 중복 인증으로 인한 유효 기간 연장 없음
        return email;
    }

    @Override
    @Transactional
    public MemberSignupResponse signUp(SignupDto dto) {

        String signupEmail = dto.email();

        if (memberRepository.existsByEmail(signupEmail))
            throw new BusinessException(ErrorCode.DUPLICATE_ERROR);

        if (!emailValidateRepository.isValidated(signupEmail)) {
            throw new BusinessException(ErrorCode.NO_VALIDATE_EMAIL);
        }

        MemberEntity member = new MemberEntity(
                dto.name(),
                signupEmail,
                encoder.encode(dto.password())
        );
        MemberEntity savedMember = memberRepository.save(member);

        // 트랜잭션 커밋 이후 이벤트가 발행되고, 트랜잭션이벤트 리스너가 이를 받아 메시지를 발행합니다.~
        eventPublisher.publishEvent(new JoinPendingEvent(savedMember));

        // TODO:RedisConnectionFailureException가 발생하면 롤백해야할 정도의 심각한 일인가?
        if (!emailValidateRepository.delete(signupEmail)){ //인증정보 삭제
            log.warn("이메일 인증 정보가 정상적으로 삭제되지 않았습니다. email = {}", signupEmail);
        }

        return new MemberSignupResponse(
                savedMember.getId(),
                savedMember.getNickname()
        );
    }

    @Override
    public LoginResultDto login(LoginDto dto) throws BusinessException {

        MemberEntity member = memberRepository.findByEmail(dto.email()).orElseThrow(
                () -> new BusinessException(ErrorCode.AUTHENTICATION_FAILED)
        );
        if (!encoder.matches(dto.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED);
        }

        String accessToken = jwtManager.generateAccessToken(member.getEmail());
        String refreshToken = jwtManager.generateRefreshToken(member.getEmail());
        boolean isTokenSaved = refreshTokenRepository.save(member.getEmail(), refreshToken, Duration.ofSeconds(jwtProperties.getRefreshExpirationSeconds()));
        if (!isTokenSaved){
            log.error("save 인자 중 하나가 null입니다. email = {}, token = {}, duration = {}",member.getEmail(), refreshToken, jwtProperties.getRefreshExpirationSeconds());
            throw new BusinessException(ErrorCode.LOGIN_FAILED, "서버 내부 요인으로 인해 로그인이 실패했습니다.");
        }

        return new LoginResultDto(
                accessToken,
                refreshToken
        );
    }

    @Override
    public LoginResultDto refresh(String refreshToken) throws BusinessException {

        String email = jwtManager.getEmailFromRefreshToken(refreshToken);
        String savedRefreshToken = refreshTokenRepository.findByEmail(email).orElseThrow(
                () -> new BusinessException(ErrorCode.REFRESH_TOKEN_ERROR)
        );

        // 현재 저장된 리프레쉬 토큰이랑 같은지 확인
        if (!savedRefreshToken.equals(refreshToken)) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_ERROR);
        }

        String newAccessToken = jwtManager.generateAccessToken(email);
        String newRefreshToken = jwtManager.generateRefreshToken(email);
        boolean isTokenSaved = refreshTokenRepository.save(email, newRefreshToken, Duration.ofSeconds(jwtProperties.getRefreshExpirationSeconds()));
        if (!isTokenSaved){
            log.error("save 인자 중 하나가 null입니다. email = {}, token = {}, duration = {}",email, newRefreshToken, jwtProperties.getRefreshExpirationSeconds());
            throw new BusinessException(ErrorCode.REFRESH_FAILED, "서버 내부 요인으로 인해 리프레싱이 실패했습니다.");
        }

        return new LoginResultDto(
                newAccessToken,
                newRefreshToken
        );
    }

    @Override
    public void logout(String refreshToken) {
        String email = jwtManager.getEmailFromRefreshToken(refreshToken);
        if (!refreshTokenRepository.deleteByEmail(email)) {
            log.warn("리프레시 토큰이 삭제되지 않았습니다! 이미 삭제 처리가 되었거나, redis에 문제가 있을 수 있습니다.");
        }
    }

    private String makeValidateMailMessage(String token) {
        // 들어가야 할 정보
        // 클릭할 URL + 메시지 내용
        String validateUrl = FRONTEND_URL + "?token=" + token;

        String message = """
                <h2>tfinder 이메일 인증</h2>
                <p><b>아래 링크를 눌러 이메일을 인증하고, 가입을 완료해주세요.</b></p>
                <p><a href="%s">참가하기</a></p>
                <p>링크가 열리지 않는다면 아래 주소를 복사해서 브라우저에 붙여넣어 주세요.</p>
                <p>%s</p>
                """.formatted(validateUrl, validateUrl);

        return message;
    }

    private Duration getEmailExpiration() {
        return Duration.ofSeconds(jwtProperties.getValidateEmailExpirationSeconds());
    }
}
