package com.thinkfree.tfinder.workspace.controller.request;

public record InviteRequest(
        long workspaceId,
        String toEmail
) {
}
