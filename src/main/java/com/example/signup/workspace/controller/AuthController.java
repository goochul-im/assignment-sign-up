package com.example.signup.workspace.controller;

import com.example.signup.workspace.service.AuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

}
