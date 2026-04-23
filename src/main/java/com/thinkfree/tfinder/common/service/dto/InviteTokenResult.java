package com.thinkfree.tfinder.common.service.dto;

public record InviteTokenResult(
        String toEmail,
        String fromEmail,
        String workspaceUrl
) {
}
