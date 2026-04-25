package com.thinkfree.tfinder.auth.service.adapter;

import com.thinkfree.tfinder.auth.service.dto.LoginDto;
import com.thinkfree.tfinder.auth.service.dto.LoginResultDto;
import com.thinkfree.tfinder.auth.service.dto.MemberSignupResultDto;
import com.thinkfree.tfinder.auth.service.dto.SignupDto;
import com.thinkfree.tfinder.auth.service.iface.IAuthUseCase;
import com.thinkfree.tfinder.auth.service.iface.IRefreshTokenRepository;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.common.service.dto.RefreshTokenResult;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import com.thinkfree.tfinder.workspace.domain.MemberType;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthUseCase {

    private final PasswordEncoder encoder;
    private final IMemberRepository memberRepository;
    private final IJwtManager jwtManager;
    private final IRefreshTokenRepository refreshTokenRepository;

    @Value("${spring.jwt.expiration.access}")
    private long JWT_ACCESS_EXPIRATION_TIME;

    @Value("${spring.jwt.expiration.refresh}")
    private long JWT_REFRESH_EXPIRATION_TIME;

    @Override
    public MemberSignupResultDto signUp(SignupDto dto) {

        if (memberRepository.existsByEmail(dto.email())) throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);

        MemberEntity member = new MemberEntity(
                dto.name(),
                dto.email(),
                encoder.encode(dto.password()),
                MemberType.DEFAULT
        );
        MemberEntity savedMember = memberRepository.save(member);

        return new MemberSignupResultDto(
                savedMember.getId(),
                savedMember.getUsername()
        );
    }

    @Override
    public LoginResultDto login(LoginDto dto) {

        MemberEntity member = memberRepository.findByEmail(dto.email()).orElseThrow(
                () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
        );
        if (!encoder.matches(dto.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_UNMATCHED);
        }

        String accessToken = jwtManager.generateAccessToken(member.getEmail(), Instant.now().plusSeconds(JWT_ACCESS_EXPIRATION_TIME));
        String refreshToken = jwtManager.generateRefreshToken(member.getEmail(), Instant.now().plusSeconds(JWT_REFRESH_EXPIRATION_TIME));
        refreshTokenRepository.save(member.getEmail(), refreshToken, Duration.ofSeconds(JWT_REFRESH_EXPIRATION_TIME));

        return new LoginResultDto(
                accessToken,
                refreshToken
        );
    }

    @Override
    public LoginResultDto refresh(String refreshToken) {

        RefreshTokenResult tokenResult = jwtManager.parsingRefreshToken(refreshToken);
        String savedRefreshToken = refreshTokenRepository.findByEmail(tokenResult.email()).orElseThrow(
                () -> new BusinessException(ErrorCode.REFRESH_TOKEN_ERROR)
        );

        if (!savedRefreshToken.equals(refreshToken)) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_ERROR);
        }

        String newAccessToken = jwtManager.generateAccessToken(tokenResult.email(), Instant.now().plusSeconds(JWT_ACCESS_EXPIRATION_TIME));
        String newRefreshToken = jwtManager.generateRefreshToken(tokenResult.email(), Instant.now().plusSeconds(JWT_REFRESH_EXPIRATION_TIME));
        refreshTokenRepository.save(tokenResult.email(), newRefreshToken, Duration.ofSeconds(JWT_REFRESH_EXPIRATION_TIME));

        return new LoginResultDto(
                newAccessToken,
                newRefreshToken
        );
    }

    @Override
    public void logout(String refreshToken) {

        RefreshTokenResult tokenResult = jwtManager.parsingRefreshToken(refreshToken);
        refreshTokenRepository.deleteByEmail(tokenResult.email());
    }
}
