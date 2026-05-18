package com.thinkfree.tfinder.auth.controller.request;

import com.thinkfree.tfinder.common.util.constant.ValidationMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(description = "로그인 이메일", example = "test@email.com")
        @NotBlank
        @Email
        String email,
        @Schema(description = "로그인 비밀번호", example = "12341234")
        @NotBlank
        String password
) {
}
