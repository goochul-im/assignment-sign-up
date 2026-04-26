package com.thinkfree.tfinder.workspace.controller.request;

import com.thinkfree.tfinder.common.constant.ValidationMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record InviteAcceptRequest(
        @Schema(description = "초대 토큰", example = "ejyPosuaQw...")
        @NotBlank(message = ValidationMessage.NOT_BLANK)
        String inviteToken
) {
}
