package com.thinkfree.tfinder.auth.controller.request;

public record SignupRequest(
        String name,
        String email,
        String password
) {
}
