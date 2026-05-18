package com.thinkfree.tfinder.common.util.jwt.dto;

public record InviteTokenResult(
        String toEmail,
        String fromEmail,
        String workspaceUrl
) {
}
