package com.example.signup.workspace.service.adapter;

import com.example.signup.workspace.infrastructure.persistence.port.IMemberRepository;
import com.example.signup.workspace.service.port.IAuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthUseCase {

    private final IMemberRepository memberRepository;

    @Override
    public void signup() {


    }
}
