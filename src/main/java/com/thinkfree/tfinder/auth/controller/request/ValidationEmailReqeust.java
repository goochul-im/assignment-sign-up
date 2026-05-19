package com.thinkfree.tfinder.auth.controller.request;

import com.thinkfree.tfinder.common.util.constant.ValidationMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 서버로부터 인증 메일을 발송해달라고 요청하는 DTO입니다
 */
public record ValidationEmailReqeust(
        @Schema(description = "인증을 요청하는 이메일", example = "example@email.com")
        @NotBlank
        @Email
        String email
) {
}
