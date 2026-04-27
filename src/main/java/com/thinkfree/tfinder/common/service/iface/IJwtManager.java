package com.thinkfree.tfinder.common.service.iface;

import com.thinkfree.tfinder.common.service.dto.InviteTokenResult;

import java.time.Instant;

public interface IJwtManager {

    String generateInviteToken(String fromEmail, String toEmail, String workspaceUrl, Instant expirationDate);

    String generateAccessToken(String memberEmail, Instant expirationDate);

    String generateRefreshToken(String memberEmail, Instant expirationDate);

    String generateValidateEmailToken(String email, Instant expirationDate);

    InviteTokenResult parsingInviteToken(String token);

    String getEmailFromAccessToken(String token);

    String getEmailFromRefreshToken(String token);

    String getEmailFromValidateEmailToken(String token);

}
