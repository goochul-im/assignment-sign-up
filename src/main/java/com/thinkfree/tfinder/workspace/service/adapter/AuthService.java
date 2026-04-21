package com.thinkfree.tfinder.workspace.service.adapter;

import com.thinkfree.tfinder.workspace.infrastructure.persistence.iface.IMemberRepository;
import com.thinkfree.tfinder.workspace.service.iface.IAuthUseCase;
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
