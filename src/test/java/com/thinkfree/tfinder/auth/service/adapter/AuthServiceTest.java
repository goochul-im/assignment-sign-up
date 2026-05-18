package com.thinkfree.tfinder.auth.service.adapter;

import com.thinkfree.tfinder.auth.controller.request.LoginRequest;
import com.thinkfree.tfinder.auth.controller.request.SignupRequest;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IEmailValidateRepository;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IPendingInviteRepository;
import com.thinkfree.tfinder.auth.service.dto.LoginResult;
import com.thinkfree.tfinder.auth.service.dto.MemberSignupResponse;
import com.thinkfree.tfinder.common.config.JwtProperties;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IRefreshTokenRepository;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.common.infrastructure.outbox.join_pending_invite.JoinPendingInviteOutboxMapper;
import com.thinkfree.tfinder.common.infrastructure.outbox.join_pending_invite.JoinPendingInvitePayload;
import com.thinkfree.tfinder.common.infrastructure.outbox.OutboxEventEntity;
import com.thinkfree.tfinder.common.infrastructure.outbox.iface.IOutboxRepository;
import com.thinkfree.tfinder.common.util.jwt.iface.IJwtManager;
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
    IOutboxRepository outboxRepository;
    @Mock
    JoinPendingInviteOutboxMapper joinPendingInviteOutboxMapper;
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
        SignupRequest dto = new SignupRequest(
                username,
                email,
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
        given(joinPendingInviteOutboxMapper.toPayload(any(JoinPendingInvitePayload.class))).willReturn("{}");

        //when
        MemberSignupResponse result = authService.signUp(dto);

        //then
        assertThat(result.memberId()).isEqualTo(returnMember.getId());
        assertThat(result.nickname()).isEqualTo(username);
        verify(outboxRepository).save(any(OutboxEventEntity.class));
    }

    @Test
    void 이미_가입된_이메일일_경우_예외를_발생시켜야_한다(){
        //given
        String email = "test@email.com";
        String name = "test";
        String passwd = "testPasswd";
        SignupRequest dto = new SignupRequest(
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
        LoginRequest dto = new LoginRequest(
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
        given(refreshTokenRepository.save(any(), any(), any())).willReturn(true);

        //when
        LoginResult result = authService.login(dto);

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
        LoginRequest dto = new LoginRequest(
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

    @Test
    void 이메일이_없다면_로그인_시_예외를_발생시켜야_한다(){
        //given
        String email = "test@email.com";
        String passwd = "testPasswd";
        LoginRequest dto = new LoginRequest(
                email,
                passwd
        );
        MemberEntity returnMember = new MemberEntity(
                1L,
                "name",
                email,
                "encodePasswd"
        );

        given(memberRepository.findByEmail(any())).willReturn(Optional.empty());

        //when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(dto));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTHENTICATION_FAILED);
    }

    @Test
    void 로그인_시_리프레쉬_토큰이_저장되지_않았다면_예외를_발생시켜야_한다(){
        //given
        String email = "test@email.com";
        String passwd = "testPasswd";
        LoginRequest dto = new LoginRequest(
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
        given(jwtManager.generateAccessToken(any())).willReturn("accessToken");
        given(jwtManager.generateRefreshToken(any())).willReturn("refreshToken");
        given(refreshTokenRepository.save(any(), any(), any())).willReturn(false);

        //when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(dto));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.LOGIN_FAILED);
    }

    @Test
    void 리프레싱_시_토큰에_저장된_이메일을_찾지_못하면_예외를_던진다(){
        //given
        String refreshToken = "testRefreshToken";

        given(jwtManager.getEmailFromRefreshToken(any())).willReturn("testEmail");
        given(refreshTokenRepository.findByEmail(any())).willReturn(Optional.empty());

        //when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.refresh(refreshToken));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REFRESH_TOKEN_ERROR);

    }

    @Test
    void 리프레싱_시_토큰에_저장된_토늨과_레디스에_저장된_토큰이_다르면_예외를_던진다(){
        //given
        String refreshToken = "testRefreshToken";

        given(jwtManager.getEmailFromRefreshToken(any())).willReturn("testEmail");
        given(refreshTokenRepository.findByEmail(any())).willReturn(Optional.of("nono"));

        //when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.refresh(refreshToken));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REFRESH_TOKEN_ERROR);

    }

    @Test
    void 리프레싱_시_토큰_저장에_실패하면_예외를_던진다(){
        //given
        String refreshToken = "testRefreshToken";

        given(jwtManager.getEmailFromRefreshToken(any())).willReturn("testEmail");
        given(refreshTokenRepository.findByEmail(any())).willReturn(Optional.of(refreshToken));
        given(jwtManager.generateAccessToken(any())).willReturn("accessToken");
        given(jwtManager.generateRefreshToken(any())).willReturn("refreshToken");
        given(refreshTokenRepository.save(any(), any(), any())).willReturn(false);

        //when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.refresh(refreshToken));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REFRESH_FAILED);
    }

    @Test
    void 리프레쉬_토큰으로_리프레싱이_가능하다(){
        //given
        String refreshToken = "testRefreshToken";

        given(jwtManager.getEmailFromRefreshToken(any())).willReturn("testEmail");
        given(refreshTokenRepository.findByEmail(any())).willReturn(Optional.of(refreshToken));
        String accessToken = "accessToken";
        given(jwtManager.generateAccessToken(any())).willReturn(accessToken);
        String newRefreshToken = "refreshToken";
        given(jwtManager.generateRefreshToken(any())).willReturn(newRefreshToken);
        given(refreshTokenRepository.save(any(), any(), any())).willReturn(true);

        //when
        LoginResult refresh = authService.refresh(refreshToken);

        //then
        assertThat(refresh.accessToken()).isEqualTo(accessToken);
        assertThat(refresh.refreshToken()).isEqualTo(newRefreshToken);
    }

}
