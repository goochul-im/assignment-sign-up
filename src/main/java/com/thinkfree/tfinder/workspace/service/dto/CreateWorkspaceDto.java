package com.thinkfree.tfinder.workspace.service.dto;

public record CreateWorkspaceDto(
        long memberId,
        String workspaceName,
        String workspaceUrl
) {
}
