package com.thinkfree.tfinder.auth.service.adapter;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IEmailValidateRepository;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IPendingInviteRepository;
import com.thinkfree.tfinder.auth.service.dto.LoginDto;
import com.thinkfree.tfinder.auth.service.dto.LoginResultDto;
import com.thinkfree.tfinder.auth.service.dto.MemberSignupResultDto;
import com.thinkfree.tfinder.auth.service.dto.SignupDto;
import com.thinkfree.tfinder.common.config.JwtProperties;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IRefreshTokenRepository;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import com.thinkfree.tfinder.common.infrastructure.external.iface.IMailSender;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PasswordEncoder encoder;
    @Mock
    private IMemberRepository memberRepository;
    @Mock
    private IJwtManager jwtManager;
    @Mock
    private IRefreshTokenRepository refreshTokenRepository;
    @Mock
    IEmailValidateRepository emailValidateRepository;
    @Mock
    IPendingInviteRepository pendingInviteRepository;
    @Mock
    IWorkspaceRepository workspaceRepository;
    @Mock
    IWorkspaceMemberRepository workspaceMemberRepository;
    @Mock
    IMailSender mailSender;
    @Mock
    ApplicationEventPublisher eventPublisher;
    @Mock
    JwtProperties jwtProperties;


    @InjectMocks
    private AuthService authService;

    @Test
    void 회원가입이_정상적으로_완료되어야_한다(){
        //given
        String email = "test@email.com";
        String username = "test";
        String passwd = "testPasswd";
        String encodePasswd = "encoded";
        SignupDto dto = new SignupDto(
                email,
                username,
                passwd
        );

        MemberEntity returnMember = new MemberEntity(
                1L,
                username,
                email,
                encodePasswd
        );

        given(memberRepository.existsByEmail(any())).willReturn(false);
        given(encoder.encode(passwd)).willReturn(encodePasswd);
        given(memberRepository.save(any())).willReturn(returnMember);
        given(emailValidateRepository.isValidated(email)).willReturn(true);

        //when
        MemberSignupResultDto result = authService.signUp(dto);

        //then
        assertThat(result.memberId()).isEqualTo(returnMember.getId());
        assertThat(result.username()).isEqualTo(username);
    }

    @Test
    void 이미_가입된_이메일일_경우_예외를_발생시켜야_한다(){
        //given
        String email = "test@email.com";
        String name = "test";
        String passwd = "testPasswd";
        SignupDto dto = new SignupDto(
                email,
                name,
                passwd
        );

        given(memberRepository.existsByEmail(any())).willReturn(true);

        //when
        assertThrows(BusinessException.class, () -> authService.signUp(dto));
    }

    @Test
    void 로그인이_완료되면_액세스_토큰을_발급해야_한다(){
        //given
        String email = "test@email.com";
        String passwd = "testPasswd";
        String accessToken = "fake accessToken";
        String refreshToken = "fake refreshToken";
        long refreshExpiredSeconds = 3000L;
        LoginDto dto = new LoginDto(
                email,
                passwd
        );
        MemberEntity returnMember = new MemberEntity(
                1L,
                "name",
                email,
                "encodePasswd"
        );

        given(memberRepository.findByEmail(any())).willReturn(Optional.of(returnMember));
        given(encoder.matches(any(), any())).willReturn(true);
        given(jwtManager.generateAccessToken(any())).willReturn(accessToken);
        given(jwtManager.generateRefreshToken(any())).willReturn(refreshToken);
        given(jwtProperties.getRefreshExpirationSeconds()).willReturn(refreshExpiredSeconds);

        //when
        LoginResultDto result = authService.login(dto);

        //then
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.refreshToken()).isEqualTo(refreshToken);
        verify(refreshTokenRepository).save(email, refreshToken, Duration.ofSeconds(refreshExpiredSeconds));
    }

    @Test
    void 비밀번호가_틀리면_예외를_발생시켜야_한다(){
        //given
        String email = "test@email.com";
        String passwd = "testPasswd";
        LoginDto dto = new LoginDto(
                email,
                passwd
        );
        MemberEntity returnMember = new MemberEntity(
                1L,
                "name",
                email,
                "encodePasswd"
        );

        given(memberRepository.findByEmail(any())).willReturn(Optional.of(returnMember));
        given(encoder.matches(any(), any())).willReturn(false);

        //when & then
        assertThrows(BusinessException.class, () -> authService.login(dto));
    }

}
