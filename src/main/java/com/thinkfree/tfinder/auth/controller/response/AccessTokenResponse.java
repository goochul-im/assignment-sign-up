package com.thinkfree.tfinder.auth.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AccessTokenResponse(
        @Schema(description = "액세스 토큰", example = "eyJhbGci....")
        String accessToken
) {
}
