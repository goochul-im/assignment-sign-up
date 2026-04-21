package com.example.signup.workspace.service.adapter;

import com.example.signup.workspace.infrastructure.persistence.iface.IMemberRepository;
import com.example.signup.workspace.service.iface.IAuthUseCase;
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
