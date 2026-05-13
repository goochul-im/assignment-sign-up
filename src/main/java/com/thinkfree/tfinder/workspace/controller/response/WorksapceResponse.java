package com.thinkfree.tfinder.workspace.controller.response;

import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.service.dto.MyWorkspacesResultDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record WorksapceResponse(
        @Schema(description = "워크스페이스 ID", example = "100")
        long workspaceId,
        @Schema(description = "워크스페이스 이름", example = "test-workspace")
        String workspaceName,
        @Schema(description = "워크스페이스 URL", example = "w-tfinder")
        String workspaceUrl,
        @Schema(description = "현재 멤버의 워크스페이스 역할", example = "OWNER")
        WorkspaceMemberRole role
) {

    public WorksapceResponse(MyWorkspacesResultDto dto) {
        this(dto.workspaceId(), dto.workspaceName(), dto.workspaceUrl(), dto.role());
    }
}
