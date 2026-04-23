package com.thinkfree.tfinder.auth.controller.dto;

public record LoginRequest(
        String email,
        String password
) {
}
