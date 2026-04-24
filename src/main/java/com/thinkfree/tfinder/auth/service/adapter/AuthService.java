package com.thinkfree.tfinder.auth.service.adapter;

import com.thinkfree.tfinder.auth.service.dto.LoginDto;
import com.thinkfree.tfinder.auth.service.dto.LoginResult;
import com.thinkfree.tfinder.auth.service.dto.SignupDto;
import com.thinkfree.tfinder.auth.service.iface.IAuthUseCase;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import com.thinkfree.tfinder.workspace.domain.MemberType;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthUseCase {

    private final PasswordEncoder encoder;
    private final IMemberRepository memberRepository;
    private final IJwtManager jwtManager;

    @Value("${spring.jwt.expiration.access}")
    private long JWT_ACCESS_EXPIRATION_TIME;

    @Value("${spring.jwt.expiration.refresh}")
    private long JWT_REFRESH_EXPIRATION_TIME;

    @Override
    public MemberEntity signUp(SignupDto dto) {

        if (memberRepository.existsByEmail(dto.email())) throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);

        MemberEntity member = new MemberEntity(
                dto.name(),
                dto.email(),
                encoder.encode(dto.password()),
                MemberType.DEFAULT
        );

        return memberRepository.save(member);
    }

    @Override
    public LoginResult login(LoginDto dto) {

        MemberEntity member = memberRepository.findByEmail(dto.email()).orElseThrow(
                () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
        );
        if (!encoder.matches(dto.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_UNMATCHED);
        }

        String accessToken = jwtManager.generateAccessToken(member.getEmail(), Instant.now().plusSeconds(JWT_ACCESS_EXPIRATION_TIME));

        return new LoginResult(
                accessToken
        );
    }
}
