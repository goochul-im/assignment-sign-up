package com.thinkfree.tfinder.auth.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberSignupResponse(
        @Schema(description = "생성된 유저의 id", example = "2")
        long memberId,
        @Schema(description = "생성된 유저의 닉네임", example = "testnickname")
        String nickname
) {
}
