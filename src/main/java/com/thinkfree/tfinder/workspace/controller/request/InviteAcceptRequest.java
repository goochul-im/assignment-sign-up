package com.thinkfree.tfinder.workspace.controller.request;

import com.thinkfree.tfinder.common.constant.ValidationMessage;
import jakarta.validation.constraints.NotBlank;

public record InviteAcceptRequest(
        @NotBlank(message = ValidationMessage.NOT_BLANK)
        String inviteToken
) {
}
