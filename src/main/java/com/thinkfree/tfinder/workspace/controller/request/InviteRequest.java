package com.thinkfree.tfinder.workspace.controller.request;

import com.thinkfree.tfinder.common.constant.ValidationMessage;
import jakarta.validation.constraints.*;

import java.util.List;

public record InviteRequest(
        @Min(value = 1, message = "id는 1이상이어야 합니다.")
        long workspaceId,

        @Size(min = 1,max = 50, message = "한번에 초대할수 있는 이메일은 1개 이상 50개 이하입니다.")
        List<
                @NotBlank(message = ValidationMessage.NOT_BLANK)
                @Email(message = ValidationMessage.INVALID_EMAIL)
                        String
                >
        toEmailList
) {
}
