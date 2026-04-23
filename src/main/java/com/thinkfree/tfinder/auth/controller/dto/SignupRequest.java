package com.thinkfree.tfinder.auth.controller.dto;

public record SignupRequest(
        String name,
        String email,
        String password
) {
}
