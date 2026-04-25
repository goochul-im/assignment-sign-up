package com.thinkfree.tfinder.auth.service.adapter;

import com.thinkfree.tfinder.auth.service.dto.LoginDto;
import com.thinkfree.tfinder.auth.service.dto.LoginResultDto;
import com.thinkfree.tfinder.auth.service.dto.MemberSignupResultDto;
import com.thinkfree.tfinder.auth.service.dto.SignupDto;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.auth.service.iface.IRefreshTokenRepository;
import com.thinkfree.tfinder.common.service.dto.RefreshTokenResult;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import com.thinkfree.tfinder.workspace.domain.MemberType;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "JWT_ACCESS_EXPIRATION_TIME", 600L);
        ReflectionTestUtils.setField(authService, "JWT_REFRESH_EXPIRATION_TIME", 604800L);
    }

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
                encodePasswd,
                MemberType.DEFAULT
        );

        when(memberRepository.existsByEmail(any())).thenReturn(false);
        when(encoder.encode(passwd)).thenReturn(encodePasswd);
        when(memberRepository.save(any())).thenReturn(returnMember);

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

        when(memberRepository.existsByEmail(any())).thenReturn(true);

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
        LoginDto dto = new LoginDto(
                email,
                passwd
        );
        MemberEntity returnMember = new MemberEntity(
                1L,
                "name",
                email,
                "encodePasswd",
                MemberType.DEFAULT
        );

        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(returnMember));
        when(encoder.matches(any(), any())).thenReturn(true);
        when(jwtManager.generateAccessToken(any(), any())).thenReturn(accessToken);
        when(jwtManager.generateRefreshToken(any(), any())).thenReturn(refreshToken);

        //when
        LoginResultDto result = authService.login(dto);

        //then
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.refreshToken()).isEqualTo(refreshToken);
        verify(refreshTokenRepository).save(email, refreshToken, Duration.ofSeconds(604800L));
    }

    @Test
    void 리프레시_토큰이_유효하면_새로운_토큰을_발급하고_저장소를_교체해야_한다(){
        //given
        String email = "test@email.com";
        String refreshToken = "old refreshToken";
        String newAccessToken = "new accessToken";
        String newRefreshToken = "new refreshToken";

        when(jwtManager.parsingRefreshToken(refreshToken)).thenReturn(new RefreshTokenResult(email));
        when(refreshTokenRepository.findByEmail(email)).thenReturn(Optional.of(refreshToken));
        when(jwtManager.generateAccessToken(any(), any())).thenReturn(newAccessToken);
        when(jwtManager.generateRefreshToken(any(), any())).thenReturn(newRefreshToken);

        //when
        LoginResultDto result = authService.refresh(refreshToken);

        //then
        assertThat(result.accessToken()).isEqualTo(newAccessToken);
        assertThat(result.refreshToken()).isEqualTo(newRefreshToken);
        verify(refreshTokenRepository).save(email, newRefreshToken, Duration.ofSeconds(604800L));
    }

    @Test
    void 저장된_리프레시_토큰과_요청_토큰이_다르면_예외를_발생시켜야_한다(){
        //given
        String email = "test@email.com";
        String refreshToken = "request refreshToken";

        when(jwtManager.parsingRefreshToken(refreshToken)).thenReturn(new RefreshTokenResult(email));
        when(refreshTokenRepository.findByEmail(email)).thenReturn(Optional.of("saved refreshToken"));

        //when & then
        assertThrows(BusinessException.class, () -> authService.refresh(refreshToken));
        verify(jwtManager, never()).generateAccessToken(any(), any());
        verify(jwtManager, never()).generateRefreshToken(any(), any());
    }

    @Test
    void 로그아웃하면_리프레시_토큰을_삭제해야_한다(){
        //given
        String email = "test@email.com";
        String refreshToken = "refreshToken";

        when(jwtManager.parsingRefreshToken(refreshToken)).thenReturn(new RefreshTokenResult(email));

        //when
        authService.logout(refreshToken);

        //then
        verify(refreshTokenRepository).deleteByEmail(email);
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
                "encodePasswd",
                MemberType.DEFAULT
        );

        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(returnMember));
        when(encoder.matches(any(), any())).thenReturn(false);

        //when & then
        assertThrows(BusinessException.class, () -> authService.login(dto));
    }

}
