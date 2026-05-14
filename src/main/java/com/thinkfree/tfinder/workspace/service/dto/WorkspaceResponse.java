package com.thinkfree.tfinder.workspace.service.dto;

import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import io.swagger.v3.oas.annotations.media.Schema;

public record WorkspaceResponse(
        @Schema(description = "워크스페이스 ID", example = "100")
        long workspaceId,
        @Schema(description = "워크스페이스 이름", example = "test-workspace")
        String workspaceName,
        @Schema(description = "워크스페이스 URL", example = "w-tfinder")
        String workspaceUrl,
        @Schema(description = "현재 멤버의 워크스페이스 역할", example = "OWNER")
        WorkspaceMemberRole role
) {

    public WorkspaceResponse(WorkspaceMemberEntity entity) {
        this(entity.getId(),
                entity.getWorkspace().getWorkspaceName(),
                entity.getWorkspace().getWorkspaceUrl(),
                entity.getRole());
    }
}
