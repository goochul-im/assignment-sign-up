package com.thinkfree.tfinder.auth.security;

public record CustomUserInfo(
        long memberId,
        String email,
        String password
) {
}
