package com.thinkfree.tfinder.workspace.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateWorkspaceResponse(
        @Schema(description = "생성된 워크스페이스 ID", example = "100")
        long workspaceId,
        @Schema(description = "생성된 워크스페이스 URL", example = "exampleUrl")
        String workspaceUrl,
        @Schema(description = "생성된 워크스페이스 이름", example = "my-workspace-name")
        String workspaceName
) {
}
