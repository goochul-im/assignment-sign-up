package com.thinkfree.tfinder.workspace.controller.request;

import com.thinkfree.tfinder.common.constant.ValidationMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InviteRequest(
        @NotBlank(message = ValidationMessage.NOT_BLANK_MESSAGE)
        long workspaceId,
        @Email(message = ValidationMessage.EMAIL_MESSAGE)
        String toEmail
) {
}
