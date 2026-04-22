package com.thinkfree.tfinder.workspace.service.iface;

import com.thinkfree.tfinder.workspace.service.dto.InviteTokenResult;

import java.time.Instant;
import java.time.LocalDateTime;

public interface IJwtManager {

    String generateInviteToken(String fromEmail, String toEmail, String workspaceUrl, Instant expirationTime);

    String generateAccessToken();

    InviteTokenResult parsingInviteToken(String token);

}
