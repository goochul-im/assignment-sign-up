package com.thinkfree.tfinder.auth.controller.request;

import com.thinkfree.tfinder.common.util.constant.ValidationMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record SignupRequest(
        @Schema(description = "회원가입할 닉네임. 2글자 이상 10글자 이하여야 하며, 한글, 숫자, 영어만 허용됩니다",example = "한글test014")
        @NotBlank
        @Size(min = 2, max = 10, message = "닉네임은 2글자 이상 10글자 이하여야 합니다")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$")
        String nickname,
        @Schema(description = "회원가입할 이메일. 비어있을수 없습니다.", example = "test@email.com")
        @NotBlank
        @Email
        String email,
        @Schema(description = "회원가입할 비밀번호. 비어있을수 없습니다. 8글자 이상 20 이하여야 합니다.", example = "1234@1234")
        @NotBlank
        @Size(min = 8, max = 20)
        String password
) {
}
