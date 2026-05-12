package com.thinkfree.tfinder.common.service.iface;

import com.thinkfree.tfinder.common.service.dto.InviteTokenResult;

import java.time.Instant;

public interface IJwtManager {

    String generateInviteToken(String fromEmail, String toEmail, String workspaceUrl);

    String generateAccessToken(String memberEmail);

    String generateRefreshToken(String memberEmail);

    String generateValidateEmailToken(String email);

    InviteTokenResult parsingInviteToken(String token);

    String getEmailFromAccessToken(String token);

    String getEmailFromRefreshToken(String token);

    String getEmailFromValidateEmailToken(String token);

}
