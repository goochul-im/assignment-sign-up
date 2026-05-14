package com.thinkfree.tfinder.workspace.service.dto;

import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;

public record WorkspaceMemberResultDto(
        long memberId,
        String nickname,
        String email,
        WorkspaceMemberRole role
) {
}
