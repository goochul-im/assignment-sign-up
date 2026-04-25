package com.thinkfree.tfinder.common.service.iface;

import com.thinkfree.tfinder.common.service.dto.AccessTokenResult;
import com.thinkfree.tfinder.common.service.dto.InviteTokenResult;
import com.thinkfree.tfinder.common.service.dto.RefreshTokenResult;

import java.time.Instant;

public interface IJwtManager {

    String generateInviteToken(String fromEmail, String toEmail, String workspaceUrl, Instant expirationDate);

    String generateAccessToken(String memberEmail, Instant expirationDate);

    String generateRefreshToken(String memberEmail, Instant expirationDate);

    InviteTokenResult parsingInviteToken(String token);

    AccessTokenResult parsingAccessToken(String token);

    RefreshTokenResult parsingRefreshToken(String token);

}
