package com.thinkfree.tfinder.workspace.controller.response;

import com.thinkfree.tfinder.workspace.service.dto.MyWorkspacesResultDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MyWorkspaceResponse(
        @Schema(description = "가입된 워크스페이스 수", example = "10")
        int size,
        @Schema(description = "가입된 워크스페이스들")
        List<MyWorkspacesResultDto> workspaceList
) {
}
