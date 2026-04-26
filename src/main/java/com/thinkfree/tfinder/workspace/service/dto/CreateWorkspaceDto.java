package com.thinkfree.tfinder.workspace.service.dto;

public record CreateWorkspaceDto(
        long requestMemberId,
        String workspaceName,
        String workspaceUrl
) {
}
