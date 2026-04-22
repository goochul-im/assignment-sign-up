package com.thinkfree.tfinder.common.service.iface;

import com.thinkfree.tfinder.workspace.service.dto.InviteTokenResult;

import java.time.Instant;

public interface IJwtManager {

    String generateInviteToken(String fromEmail, String toEmail, String workspaceUrl, Instant expirationTime);

    String generateAccessToken(Long memberId, Instant expirationTime);

    InviteTokenResult parsingInviteToken(String token);

}
