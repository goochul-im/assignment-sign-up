package com.thinkfree.tfinder.auth.controller.request;

import com.thinkfree.tfinder.common.constant.ValidationMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailValidateReqeust(
        @Schema(description = "인증을 요청하는 이메일", example = "example@email.com")
        @NotBlank(message = ValidationMessage.NOT_BLANK)
        @Email(message = ValidationMessage.INVALID_EMAIL)
        String email
) {
}
