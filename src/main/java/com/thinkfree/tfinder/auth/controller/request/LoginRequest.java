package com.thinkfree.tfinder.auth.controller.request;

import com.thinkfree.tfinder.common.constant.ValidationMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotBlank(message = ValidationMessage.NOT_BLANK_MESSAGE)
        @Email(message = "이메일 형식이 올바르지 않습니다")
        String email,
        @NotBlank(message = ValidationMessage.NOT_BLANK_MESSAGE)
        String password
) {
}
