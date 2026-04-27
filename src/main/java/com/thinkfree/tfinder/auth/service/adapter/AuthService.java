package com.thinkfree.tfinder.auth.service.adapter;

import com.thinkfree.tfinder.auth.service.dto.LoginDto;
import com.thinkfree.tfinder.auth.service.dto.LoginResultDto;
import com.thinkfree.tfinder.auth.service.dto.MemberSignupResultDto;
import com.thinkfree.tfinder.auth.service.dto.SignupDto;
import com.thinkfree.tfinder.auth.service.iface.IAuthUseCase;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IEmailValidateRepository;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IPendingInviteRepository;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IRefreshTokenRepository;
import com.thinkfree.tfinder.common.config.JwtProperties;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.infrastructure.external.iface.IMailSender;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthUseCase {

    private final PasswordEncoder encoder;
    private final IMemberRepository memberRepository;
    private final IJwtManager jwtManager;
    private final IRefreshTokenRepository refreshTokenRepository;
    private final IEmailValidateRepository emailValidateRepository;
    private final IPendingInviteRepository pendingInviteRepository;
    private final IWorkspaceRepository workspaceRepository;
    private final IWorkspaceMemberRepository workspaceMemberRepository;
    private final IMailSender mailSender;
    private final JwtProperties jwtProperties;

    @Value("${frontend.url}")
    private String FRONTEND_URL;

    @Override
    public void emailValidateRequest(String email) {
        if (memberRepository.existsByEmail(email))
            throw new BusinessException(ErrorCode.DUPLICATE_ERROR);

        String token = jwtManager.generateValidateEmailToken(email, Instant.now().plusSeconds(jwtProperties.getValidateEmailExpirationSeconds()));

        try {
            emailValidateRepository.save(
                    email,
                    Duration.ofSeconds(jwtProperties.getValidateEmailExpirationSeconds())
            );
            mailSender.asyncSend(
                    email,
                    "이메일 인증 요청",
                    makeValidateMailMessage(token)
            );
        } catch (Exception e) {
            emailValidateRepository.delete(email);
            throw e;
        }

    }

    @Override
    public void emailValidate(String token) throws BusinessException {
        String email = jwtManager.getEmailFromValidateEmailToken(token);

        if (!emailValidateRepository.isValidate(email))
            throw new BusinessException(ErrorCode.NO_VALIDATE_EMAIL);
    }

    @Override
    @Transactional
    public MemberSignupResultDto signUp(SignupDto dto) {

        String signupEmail = dto.email();
        if (memberRepository.existsByEmail(signupEmail))
            throw new BusinessException(ErrorCode.DUPLICATE_ERROR);

        if (!emailValidateRepository.isValidate(signupEmail)) {
            throw new BusinessException(ErrorCode.NO_VALIDATE_EMAIL);
        }

        MemberEntity member = new MemberEntity(
                dto.name(),
                signupEmail,
                encoder.encode(dto.password())
        );
        MemberEntity savedMember = memberRepository.save(member);

        joinPendingInvites(signupEmail, savedMember);

        // 이메일 인증정보, 워크스페이스 대기 정보 삭제
        emailValidateRepository.delete(signupEmail);
        pendingInviteRepository.delete(signupEmail);

        return new MemberSignupResultDto(
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

        String accessToken = jwtManager.generateAccessToken(member.getEmail(), Instant.now().plusSeconds(jwtProperties.getAccessExpirationSeconds()));
        String refreshToken = jwtManager.generateRefreshToken(member.getEmail(), Instant.now().plusSeconds(jwtProperties.getRefreshExpirationSeconds()));
        refreshTokenRepository.save(member.getEmail(), refreshToken, Duration.ofSeconds(jwtProperties.getRefreshExpirationSeconds()));

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

        String newAccessToken = jwtManager.generateAccessToken(email, Instant.now().plusSeconds(jwtProperties.getAccessExpirationSeconds()));
        String newRefreshToken = jwtManager.generateRefreshToken(email, Instant.now().plusSeconds(jwtProperties.getRefreshExpirationSeconds()));
        refreshTokenRepository.save(email, newRefreshToken, Duration.ofSeconds(jwtProperties.getRefreshExpirationSeconds()));

        return new LoginResultDto(
                newAccessToken,
                newRefreshToken
        );
    }

    @Override
    public void logout(String refreshToken) {
        String email = jwtManager.getEmailFromRefreshToken(refreshToken);
        refreshTokenRepository.deleteByEmail(email);
    }

    private void joinPendingInvites(String email, MemberEntity member) {
        Set<String> pendingWorkspaceUrls = pendingInviteRepository.findWorkspaceUrlsByEmail(email);
        // redis에서 가져오는 걸 실패할떄는 어떻게 하지??
        // 아... 이거 쿼리가 너무 많이 나갈수 있겠는데... In으로 바꿔야할듯?

        for (String workspaceUrl : pendingWorkspaceUrls) {
            WorkspaceEntity workspace = workspaceRepository.findByWorkspaceUrl(workspaceUrl).orElseThrow(
                    () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
            );

            if (!workspaceMemberRepository.existsByWorkspaceAndMember(workspace, member)) {
                workspaceMemberRepository.save(new WorkspaceMemberEntity(
                        workspace,
                        member,
                        WorkspaceMemberRole.MEMBER
                ));
            }
        }
    }

    private String makeValidateMailMessage(String token) {
        // 들어가야 할 정보
        // 클릭할 URL + 메시지 내용
        String validateToken = FRONTEND_URL + "?token=" + token;

        StringBuilder sb = new StringBuilder()
                .append("<h2>tfinder 이메일 인증</h2>")
                .append("<p><b>")
                .append("<p>아래 링크를 눌러 이메일을 인증하세요</p>")
                .append("<p>서비스의 회원이 아니라면, 아래 링크를 누르고 10분 이내로 가입해주세요</p>")
                .append("<p>")
                .append("<a href=\"")
                .append(validateToken)
                .append("\">참가하기</a>")
                .append("</p>")
                .append("<p>링크가 열리지 않는다면 아래 주소를 복사해서 브라우저에 붙여넣어 주세요.</p>")
                .append("<p>")
                .append(validateToken)
                .append("</p>");

        return sb.toString();
    }
}
