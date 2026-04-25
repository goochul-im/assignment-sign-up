package com.thinkfree.tfinder.auth.service.dto;

/**
 * 필드가 한개라도 필요한 이유? -> 응답으로 바로 나가서
 */
public record LoginResultDto(
        String accessToken,
        String refreshToken
) {
}
