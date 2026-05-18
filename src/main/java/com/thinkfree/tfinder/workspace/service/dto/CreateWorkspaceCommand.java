package com.thinkfree.tfinder.workspace.service.dto;

public record CreateWorkspaceCommand(
        long requestMemberId,
        String workspaceName,
        String workspaceUrl
) {
}
