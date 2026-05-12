package com.thinkfree.tfinder.auth.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ValidateEmailResponse(
        @Schema(description = "해당 토큰으로 인증된 이메일입니다.", example = "test123@email.com")
        String validateEmail
) {
}
