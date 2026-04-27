package com.thinkfree.tfinder.auth.controller.request;

import com.thinkfree.tfinder.common.constant.ValidationMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailValidateReqeust(
        @NotBlank(message = ValidationMessage.NOT_BLANK)
        @Email(message = ValidationMessage.INVALID_EMAIL)
        String email
) {
}
