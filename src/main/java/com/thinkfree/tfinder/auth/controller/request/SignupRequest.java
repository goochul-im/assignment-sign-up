package com.thinkfree.tfinder.auth.controller.request;

import jakarta.validation.constraints.*;

public record SignupRequest(
        @NotBlank
        @Size(min = 2, max = 10, message = "이름은 2글자 이상 10글자 이하여야 합니다")
        //TODO: 특수문자 제거
        String username,
        @Email(message = "이메일 형식이 올바르지 않습니다")
        String email,
        @NotBlank
        @Size(min = 8, max = 20, message = "비밀번호는 8글자 이상 20글자 이하여야 합니다")
        String password
) {
}
