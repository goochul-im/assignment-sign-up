package com.thinkfree.tfinder.auth.service.iface;

import com.thinkfree.tfinder.auth.service.dto.LoginDto;
import com.thinkfree.tfinder.auth.service.dto.LoginResultDto;
import com.thinkfree.tfinder.auth.service.dto.MemberSignupResultDto;
import com.thinkfree.tfinder.auth.service.dto.SignupDto;
import com.thinkfree.tfinder.common.exception.BusinessException;

public interface IAuthUseCase {

    /**
     * 이메일 인증 요청
     * @param email 인증을 요청할 이메일
     * @throws BusinessException 이미 가입된 이메일
     */
    void emailValidateRequest(String email) throws BusinessException;

    /**
     * 이메일 인증
     *
     * @param token 이메일 인증을 위한 토큰
     * @throws BusinessException
     */
    void emailValidate(String token) throws BusinessException;

    /**
     * 사용자가 회원가입을 요청할 때, 만약 워크스페이스 초대 대기 상태라면 회원가입 후 자동으로 참가됩니다.
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

    /**
     * 액세스 토큰과 리프레쉬 토큰을 리프레쉬할 때 사용
     * @param refreshToken 리프레쉬 토큰 값
     * @return 새로 발급된 액세스 토큰 + 리프레쉬 토큰
     * @throws BusinessException 리프레쉬 토큰 불일치
     */
    LoginResultDto refresh(String refreshToken) throws BusinessException;

    /**
     * 리프레쉬 토큰 무효화. 액세스 토큰은 프론트에서 삭제 처리
     * @param refreshToken 리프레쉬 토큰 값
     */
    void logout(String refreshToken);

}
