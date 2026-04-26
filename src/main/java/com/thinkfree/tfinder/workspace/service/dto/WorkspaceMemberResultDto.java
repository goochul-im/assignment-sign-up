package com.thinkfree.tfinder.workspace.service.dto;

import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import io.swagger.v3.oas.annotations.media.Schema;

public record WorkspaceMemberResultDto(
        @Schema(description = "멤버 ID", example = "1")
        long memberId,
        @Schema(description = "닉네임", example = "testname")
        String nickname,
        @Schema(description = "이메일", example = "test@example.com")
        String email,
        @Schema(description = "워크스페이스 역할", example = "OWNER")
        WorkspaceMemberRole role
) {
}
