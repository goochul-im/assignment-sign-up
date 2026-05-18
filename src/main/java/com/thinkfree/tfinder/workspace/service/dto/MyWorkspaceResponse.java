package com.thinkfree.tfinder.workspace.service.dto;

import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MyWorkspaceResponse(
        @Schema(description = "가입된 워크스페이스 수", example = "10")
        int size,
        @Schema(description = "가입된 워크스페이스들")
        List<WorkspaceResponse> workspaceList
) {

        public MyWorkspaceResponse(List<WorkspaceMemberEntity> workspaceList) {
            this(workspaceList.size(), workspaceList.stream().map(WorkspaceResponse::new).toList());
        }

}
