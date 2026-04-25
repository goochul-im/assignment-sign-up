package com.thinkfree.tfinder.auth.service.iface;

import com.thinkfree.tfinder.auth.service.dto.LoginDto;
import com.thinkfree.tfinder.auth.service.dto.LoginResultDto;
import com.thinkfree.tfinder.auth.service.dto.MemberSignupResultDto;
import com.thinkfree.tfinder.auth.service.dto.SignupDto;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;

public interface IAuthUseCase {

    /**
     * 사용자 회원가입 유즈케이스
     * @param dto 회원가입 요청 정보
     * @return 생성된 멤버 정보, 가입 이후 다른 정보를 바로 요청하거나 화면에 보여줘야 할 때 수정 가능
     * @throws BusinessException 이메일 중복
     */
    MemberSignupResultDto signUp(SignupDto dto) throws BusinessException;

    /**
     * 사용자 로그인 유즈케이스
     * @param dto 로그인 요청 정보
     * @return 로그인한 사용자의 액세스 토큰 정보
     * @throws BusinessException 가입되지 않은 이메일 정보, 비밀번호 불일치
     */
    LoginResultDto login(LoginDto dto) throws BusinessException;

    LoginResultDto refresh(String refreshToken) throws BusinessException;

    void logout(String refreshToken) throws BusinessException;

}
