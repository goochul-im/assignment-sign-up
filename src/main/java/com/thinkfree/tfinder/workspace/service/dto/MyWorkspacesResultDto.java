package com.thinkfree.tfinder.workspace.service.dto;

import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import io.swagger.v3.oas.annotations.media.Schema;

public record MyWorkspacesResultDto(
        long workspaceId,
        String workspaceName,
        String workspaceUrl,
        WorkspaceMemberRole role
) {
}
