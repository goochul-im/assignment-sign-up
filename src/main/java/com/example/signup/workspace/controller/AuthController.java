package com.example.signup.workspace.controller;

import com.example.signup.workspace.service.port.IAuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final IAuthUseCase authUseCase;

}
