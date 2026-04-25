package com.thinkfree.tfinder.workspace.controller.request;

import com.thinkfree.tfinder.common.constant.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record WorkspaceCreateRequest(
        @NotBlank(message = ValidationMessage.NOT_BLANK)
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = ValidationMessage.ONLY_ENG_KOR_NUM_PATTERN)
        String workspaceName,
        @NotBlank(message = ValidationMessage.NOT_BLANK)
        String workspaceUrl
) {
}
