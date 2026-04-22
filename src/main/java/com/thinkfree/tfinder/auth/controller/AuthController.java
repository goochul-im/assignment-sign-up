package com.thinkfree.tfinder.auth.controller;

import com.thinkfree.tfinder.workspace.service.iface.IAuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final IAuthUseCase authUseCase;

}
