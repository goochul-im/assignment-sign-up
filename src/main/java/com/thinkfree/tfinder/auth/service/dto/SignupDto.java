package com.thinkfree.tfinder.auth.service.dto;

public record SignupDto(
        String email,
        String name,
        String password
) {
}
