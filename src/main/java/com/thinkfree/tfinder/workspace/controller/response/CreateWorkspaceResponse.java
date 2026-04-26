package com.thinkfree.tfinder.workspace.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateWorkspaceResponse(
        @Schema(description = "생성된 워크스페이스 ID", example = "100")
        long workspaceId
) {
}
