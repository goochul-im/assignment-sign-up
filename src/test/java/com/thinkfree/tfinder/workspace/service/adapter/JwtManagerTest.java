package com.thinkfree.tfinder.workspace.service.adapter;

import com.thinkfree.tfinder.common.service.adpater.JwtManager;
import com.thinkfree.tfinder.common.service.dto.InviteTokenResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtManagerTest {

    private static JwtManager jwtManager;

    @BeforeAll
    static void before() {
        jwtManager = new JwtManager("my-secret-key-Lorem-ipsum-dolor-sit-amet-sollicitudin-vel-dapibus-magna-condimentum. ");
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

        //when & then
        assertThrows(RuntimeException.class,() -> jwtManager.parsingInviteToken(token));

    }

    @Test
    void 초대_토큰이_아닐_경우_예외를_반환해야_한다(){
        //given
        Instant expTime = Instant.now().plusSeconds(1000);
        String token = jwtManager.generateAccessToken("test@email.com", expTime);

        //when & then
        assertThrows(RuntimeException.class, () -> jwtManager.parsingInviteToken(token));
    }


}
