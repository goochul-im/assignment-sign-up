package com.thinkfree.tfinder.workspace.controller.response;

import com.thinkfree.tfinder.workspace.service.dto.WorkspaceMemberResultDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record WorkspaceMembersResponse(
        @Schema(description = "워크스페이스 멤버 수", example = "3")
        int size,
        @Schema(description = "워크스페이스 멤버 목록")
        List<WorkspaceMemberResultDto> memberList
) {
}
