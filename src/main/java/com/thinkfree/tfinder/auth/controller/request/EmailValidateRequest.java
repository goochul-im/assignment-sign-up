package com.thinkfree.tfinder.auth.controller.request;

import com.thinkfree.tfinder.common.util.constant.ValidationMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 인증 요청을 이미 보내고, 대기중인 인증 정보를 인증하기 위한 DTO
 */
public record EmailValidateRequest(
        @Schema(description = "인증 토큰", example = "ejyPosuaQw...")
        @NotBlank(message = ValidationMessage.NOT_BLANK)
        String token
) {
}
