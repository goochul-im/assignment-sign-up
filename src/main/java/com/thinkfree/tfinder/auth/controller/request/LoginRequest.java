package com.thinkfree.tfinder.auth.controller.request;

public record LoginRequest(
        String email,
        String password
) {
}
