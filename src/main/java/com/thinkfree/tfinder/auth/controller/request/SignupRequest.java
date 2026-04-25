package com.thinkfree.tfinder.auth.controller.request;

import com.thinkfree.tfinder.common.constant.ValidationMessage;
import jakarta.validation.constraints.*;

public record SignupRequest(
        @NotBlank(message = ValidationMessage.NOT_BLANK_MESSAGE)
        @Size(min = 2, max = 10, message = "이름은 2글자 이상 10글자 이하여야 합니다")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = ValidationMessage.SPECIAL_CHAR_MESSAGE)
        String username,
        @NotBlank(message = ValidationMessage.NOT_BLANK_MESSAGE)
        @Email(message = ValidationMessage.EMAIL_MESSAGE)
        String email,
        @NotBlank(message = ValidationMessage.NOT_BLANK_MESSAGE)
        @Size(min = 8, max = 20, message = "비밀번호는 8글자 이상 20글자 이하여야 합니다")
        String password
) {
}
