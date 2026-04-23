package com.thinkfree.tfinder.auth.controller;

import com.thinkfree.tfinder.auth.service.iface.IAuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final IAuthUseCase authUseCase;

}
