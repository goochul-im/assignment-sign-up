package com.thinkfree.tfinder.auth.service.adapter;

import com.thinkfree.tfinder.auth.service.dto.SignupDto;
import com.thinkfree.tfinder.auth.service.iface.IAuthUseCase;
import com.thinkfree.tfinder.workspace.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthUseCase {

    @Override
    public Member signUp(SignupDto dto) {
        return null;
    }

}
