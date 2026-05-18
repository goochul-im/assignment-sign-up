package com.thinkfree.tfinder.workspace.service.adapter;

import com.thinkfree.tfinder.common.config.JwtProperties;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.common.util.jwt.adpater.JwtManager;
import com.thinkfree.tfinder.common.util.jwt.dto.InviteTokenResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtManagerTest {

    private static final String TEST_SECRET_KEY = "test-secret-key-must-be-at-least-32-bytes";

    private JwtManager jwtManager() {
        return jwtManager(600, 604800, 172800, 600);
    }

    private JwtManager expiredAccessJwtManager() {
        return jwtManager(-1, 604800, 172800, 600);
    }

    private JwtManager expiredRefreshJwtManager() {
        return jwtManager(600, -1, 172800, 600);
    }

    private JwtManager expiredInviteJwtManager() {
        return jwtManager(600, 604800, -1, 600);
    }

    private JwtManager jwtManager(
            long accessExpirationSeconds,
            long refreshExpirationSeconds,
            long inviteExpirationSeconds,
            long validateEmailExpirationSeconds
    ) {
        return new JwtManager(new JwtProperties(
                TEST_SECRET_KEY,
                accessExpirationSeconds,
                refreshExpirationSeconds,
                inviteExpirationSeconds,
                validateEmailExpirationSeconds
        ));
    }

    @Test
    void 초대_토큰이_정상적으로_만들어지고_정보를_파싱할수_있어야_한다(){
        //given
        JwtManager jwtManager = jwtManager();
        String to = "to@email.com";
        String from = "from@email.com";
        String url = "1234-1234";
        String token = jwtManager.generateInviteToken(from, to, url);

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
        JwtManager jwtManager = expiredInviteJwtManager();
        String to = "to@email.com";
        String from = "from@email.com";
        String url = "1234-1234";
        String token = jwtManager.generateInviteToken(from, to, url);

        //when
        BusinessException exception = assertThrows(BusinessException.class, () -> jwtManager.parsingInviteToken(token));

        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVITE_TOKEN_ERROR);
    }

    @Test
    void 초대_토큰이_아닐_경우_예외를_반환해야_한다(){
        //given
        JwtManager jwtManager = jwtManager();
        String token = jwtManager.generateAccessToken("test@email.com");

        //when
        BusinessException exception = assertThrows(BusinessException.class, () -> jwtManager.parsingInviteToken(token));

        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVITE_TOKEN_ERROR);
    }

    @Test
    void 액세스_토큰이_정상적으로_만들어지고_정보를_파싱할수_있어야_한다(){
        //given
        JwtManager jwtManager = jwtManager();
        String email = "test@email.com";
        String accessToken = jwtManager.generateAccessToken(email);

        //when
        String result = jwtManager.getEmailFromAccessToken(accessToken);

        //then
        assertThat(result).isEqualTo(email);
    }

    @Test
    void 액세스_토큰이_만료될_경우_만료_예외를_던져야_한다(){
        //given
        JwtManager jwtManager = expiredAccessJwtManager();
        String email = "test@email.com";
        String accessToken = jwtManager.generateAccessToken(email);

        //when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> jwtManager.getEmailFromAccessToken(accessToken));

        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ACCESS_TOKEN_EXPIRED_ERROR);
    }

    @Test
    void 액세스_토큰이_올바르지_않을_경우_예외를_던져야_한다(){
        //given
        JwtManager jwtManager = jwtManager();
        String email = "test@email.com";
        String accessToken = jwtManager.generateRefreshToken(email);
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
        JwtManager jwtManager = jwtManager();
        String email = "test@email.com";
        String refreshToken = jwtManager.generateRefreshToken(email);

        //when
        String result = jwtManager.getEmailFromRefreshToken(refreshToken);

        //then
        assertThat(result).isEqualTo(email);
    }

    @Test
    void 리프레쉬_토큰이_만료될_경우_만료_예외를_던져야_한다(){
        //given
        JwtManager jwtManager = expiredRefreshJwtManager();
        String email = "test@email.com";
        String refreshToken = jwtManager.generateRefreshToken(email);

        //when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> jwtManager.getEmailFromRefreshToken(refreshToken));

        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REFRESH_TOKEN_EXPIRED_ERROR);
    }

    @Test
    void 리프레쉬_토큰이_올바르지_않을_경우_예외를_던져야_한다(){
        //given
        JwtManager jwtManager = jwtManager();
        String email = "test@email.com";
        String refreshToken = jwtManager.generateRefreshToken(email);
        String invalidRefreshToken = refreshToken.substring(3);

        //when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> jwtManager.getEmailFromRefreshToken(invalidRefreshToken));

        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REFRESH_TOKEN_ERROR);
    }

}
