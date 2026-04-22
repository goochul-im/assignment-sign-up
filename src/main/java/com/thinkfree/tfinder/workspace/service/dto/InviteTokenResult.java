package com.thinkfree.tfinder.workspace.service.dto;

import java.time.Instant;

public record InviteTokenResult(
        String toEmail,
        String fromEmail,
        String workspaceUrl
) {
}
