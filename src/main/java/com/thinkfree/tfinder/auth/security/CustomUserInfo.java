package com.thinkfree.tfinder.auth.security;

public record CustomUserInfo(
        Long memberId,
        String email,
        String password
) {
}
