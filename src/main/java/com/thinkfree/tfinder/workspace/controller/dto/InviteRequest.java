package com.thinkfree.tfinder.workspace.controller.dto;

public record InviteRequest(
        Long workspaceId,
        String toEmail
) {
}
