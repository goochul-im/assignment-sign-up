package com.thinkfree.tfinder.workspace.controller.request;

import com.thinkfree.tfinder.common.constant.ValidationMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

public record InviteRequest(
        @Schema(description = "초대할 워크스페이스 id, 음수는 허용되지 않습니다.", example = "1004")
        @Min(value = 1, message = "id는 1이상이어야 합니다.")
        long workspaceId,

        @Schema(description = "초대할 이메일 리스트, 1개 이상 50개 이하여야 합니다", example = "[\"test1@email.com\", \"test2@email.com\"]")
        @Size(min = 1,max = 50, message = "한번에 초대할수 있는 이메일은 1개 이상 50개 이하입니다.")
        List<
                @NotBlank(message = ValidationMessage.NOT_BLANK)
                @Email(message = ValidationMessage.INVALID_EMAIL)
                        String
                >
        toEmailList
) {
}
