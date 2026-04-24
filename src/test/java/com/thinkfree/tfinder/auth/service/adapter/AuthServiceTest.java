package com.thinkfree.tfinder.auth.service.adapter;

import com.thinkfree.tfinder.auth.service.dto.LoginDto;
import com.thinkfree.tfinder.auth.service.dto.LoginResult;
import com.thinkfree.tfinder.auth.service.dto.SignupDto;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import com.thinkfree.tfinder.workspace.domain.MemberType;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @InjectMocks
    private AuthService authService;

    @Test
    void 회원가입이_정상적으로_완료되어야_한다(){
        //given
        String email = "test@email.com";
        String name = "test";
        String passwd = "testPasswd";
        String encodePasswd = "encoded";
        SignupDto dto = new SignupDto(
                email,
                name,
                passwd
        );

        MemberEntity returnMember = new MemberEntity(
                1L,
                name,
                email,
                encodePasswd,
                MemberType.DEFAULT
        );

        when(memberRepository.existsByEmail(any())).thenReturn(false);
        when(encoder.encode(passwd)).thenReturn(encodePasswd);
        when(memberRepository.save(any())).thenReturn(returnMember);

        //when
        MemberEntity member = authService.signUp(dto);

        //then
        assertThat(member.getId()).isNotNull();
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getName()).isEqualTo(name);
        assertThat(member.getPassword()).isEqualTo(encodePasswd);
        assertThat(member.getMemberType()).isEqualTo(MemberType.DEFAULT);
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

        //when
        LoginResult result = authService.login(dto);

        //then
        assertThat(result.accessToken()).isEqualTo(accessToken);
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
