package com.thinkfree.tfinder.auth.service.iface;

import com.thinkfree.tfinder.auth.service.dto.LoginDto;
import com.thinkfree.tfinder.auth.service.dto.LoginResult;
import com.thinkfree.tfinder.auth.service.dto.SignupDto;
import com.thinkfree.tfinder.workspace.domain.Member;

public interface IAuthUseCase {

    Member signUp(SignupDto dto);

    LoginResult login(LoginDto dto);

}
