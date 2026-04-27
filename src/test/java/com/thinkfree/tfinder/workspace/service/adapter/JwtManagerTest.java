package com.thinkfree.tfinder.workspace.service.adapter;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.common.config.JwtProperties;
import com.thinkfree.tfinder.common.service.adpater.JwtManager;
import com.thinkfree.tfinder.common.service.dto.AccessTokenResult;
import com.thinkfree.tfinder.common.service.dto.InviteTokenResult;
import com.thinkfree.tfinder.common.service.dto.RefreshTokenResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtManagerTest {

    private static JwtManager jwtManager;

    @BeforeAll
    static void before() {
        jwtManager = new JwtManager(new JwtProperties(
                "my-secret-key-Lorem-ipsum-dolor-sit-amet-sollicitudin-vel-dapibus-magna-condimentum. ",
                600,
                604800,
                172800,
                600
        ));
    }

    @Test
    void 초대_토큰이_정상적으로_만들어지고_정보를_파싱할수_있어야_한다(){
        //given
        String to = "to@email.com";
        String from = "from@email.com";
        String url = "1234-1234";
        Instant expTime = Instant.now().plusSeconds(1000);
        String token = jwtManager.generateInviteToken(from, to, url, expTime);

        //when
        InviteTokenResult result = jwtManager.parsingInviteToken(token);

        //then
        assertThat(result.fromEmail()).isEqualTo(from);
        assertThat(result.toEmail()).isEqualTo(to);
        assertThat(result.workspaceUrl()).isEqualTo(url);
    }

    @Test
    void 초대_토큰이_만료되었을_경우_예외를_반환해야_한다(){
        //given
        String to = "to@email.com";
        String from = "from@email.com";
        String url = "1234-1234";
        Instant expTime = Instant.now().minusSeconds(1000);
        String token = jwtManager.generateInviteToken(from, to, url, expTime);

        //when
        BusinessException exception = assertThrows(BusinessException.class, () -> jwtManager.parsingInviteToken(token));

        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVITE_TOKEN_ERROR);
    }

    @Test
    void 초대_토큰이_아닐_경우_예외를_반환해야_한다(){
        //given
        Instant expTime = Instant.now().plusSeconds(1000);
        String token = jwtManager.generateAccessToken("test@email.com", expTime);

        //when
        BusinessException exception = assertThrows(BusinessException.class, () -> jwtManager.parsingInviteToken(token));

        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVITE_TOKEN_ERROR);
    }

    @Test
    void 액세스_토큰이_정상적으로_만들어지고_정보를_파싱할수_있어야_한다(){
        //given
        String email = "test@email.com";
        Instant date = Instant.now().plusSeconds(1000);
        String accessToken = jwtManager.generateAccessToken(email, date);

        //when
        String result = jwtManager.getEmailFromAccessToken(accessToken);

        //then
        assertThat(result).isEqualTo(email);
    }

    @Test
    void 액세스_토큰이_만료될_경우_만료_예외를_던져야_한다(){
        //given
        String email = "test@email.com";
        Instant date = Instant.now().minusSeconds(1000);
        String accessToken = jwtManager.generateAccessToken(email, date);

        //when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> jwtManager.getEmailFromAccessToken(accessToken));

        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ACCESS_TOKEN_EXPIRED_ERROR);
    }

    @Test
    void 액세스_토큰이_올바르지_않을_경우_예외를_던져야_한다(){
        //given
        String email = "test@email.com";
        Instant date = Instant.now().minusSeconds(1000);
        String accessToken = jwtManager.generateRefreshToken(email, date);
        String invalidAccessToken = accessToken.substring(3);

        //when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> jwtManager.getEmailFromAccessToken(invalidAccessToken));

        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ACCESS_TOKEN_ERROR);
    }

    @Test
    void 리프레쉬_토큰이_정상적으로_만들어지고_정보를_파싱할수_있어야_한다(){
        //given
        String email = "test@email.com";
        Instant date = Instant.now().plusSeconds(1000);
        String refreshToken = jwtManager.generateRefreshToken(email, date);

        //when
        String result = jwtManager.getEmailFromRefreshToken(refreshToken);

        //then
        assertThat(result).isEqualTo(email);
    }

    @Test
    void 리프레쉬_토큰이_만료될_경우_만료_예외를_던져야_한다(){
        //given
        String email = "test@email.com";
        Instant date = Instant.now().minusSeconds(1000);
        String refreshToken = jwtManager.generateRefreshToken(email, date);

        //when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> jwtManager.getEmailFromRefreshToken(refreshToken));

        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REFRESH_TOKEN_EXPIRED_ERROR);
    }

    @Test
    void 리프레쉬_토큰이_올바르지_않을_경우_예외를_던져야_한다(){
        //given
        String email = "test@email.com";
        Instant date = Instant.now().minusSeconds(1000);
        String refreshToken = jwtManager.generateRefreshToken(email, date);
        String invalidRefreshToken = refreshToken.substring(3);

        //when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> jwtManager.getEmailFromRefreshToken(invalidRefreshToken));

        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REFRESH_TOKEN_ERROR);
    }

}
