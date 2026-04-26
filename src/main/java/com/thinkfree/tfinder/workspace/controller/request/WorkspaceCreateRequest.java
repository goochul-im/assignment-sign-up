package com.thinkfree.tfinder.workspace.controller.request;

import com.thinkfree.tfinder.common.constant.ValidationMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record WorkspaceCreateRequest(
        @Schema(description = "생성할 워크스페이스 이름. 영어, 숫자, 한글 이외에는 허용되지 않습니다.", example = "test-workspace")
        @NotBlank(message = ValidationMessage.NOT_BLANK)
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = ValidationMessage.ONLY_ENG_KOR_NUM_PATTERN)
        String workspaceName,
        @Schema(description = "생성할 워크스페이스 Url", example = "w-tfinder")
        @NotBlank(message = ValidationMessage.NOT_BLANK)
        String workspaceUrl
) {
}
