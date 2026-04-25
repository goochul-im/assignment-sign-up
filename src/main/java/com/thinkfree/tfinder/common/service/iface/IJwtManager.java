package com.thinkfree.tfinder.common.service.iface;

import com.thinkfree.tfinder.common.service.dto.AccessTokenResult;
import com.thinkfree.tfinder.common.service.dto.InviteTokenResult;

import java.time.Instant;

public interface IJwtManager {

    String generateInviteToken(String fromEmail, String toEmail, String workspaceUrl, Instant expirationDate);

    String generateAccessToken(String memberEmail, Instant expirationDate);

    InviteTokenResult parsingInviteToken(String token);

    AccessTokenResult parsingAccessToken(String token);

}
