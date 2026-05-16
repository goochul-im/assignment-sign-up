package com.thinkfree.tfinder.common.service.iface;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.service.dto.InviteTokenResult;

public interface IJwtManager {

    /**
     * 워크스페이스 초대 토큰을 생성합니다.
     * @param fromEmail 초대한 멤버의 이메일
     * @param toEmail 초대받을 이메일
     * @param workspaceUrl 초대할 워크스페이스 URL
     * @return 생성된 워크스페이스 초대 토큰
     */
    String generateInviteToken(String fromEmail, String toEmail, String workspaceUrl);

    /**
     * 멤버 이메일을 담은 액세스 토큰을 생성합니다.
     * @param memberEmail 토큰에 담을 멤버 이메일
     * @return 생성된 액세스 토큰
     */
    String generateAccessToken(String memberEmail);

    /**
     * 멤버 이메일을 담은 리프레쉬 토큰을 생성합니다.
     * @param memberEmail 토큰에 담을 멤버 이메일
     * @return 생성된 리프레쉬 토큰
     */
    String generateRefreshToken(String memberEmail);

    /**
     * 이메일 인증 토큰을 생성합니다.
     * @param email 인증할 이메일
     * @return 생성된 이메일 인증 토큰
     */
    String generateValidateEmailToken(String email);

    /**
     * 워크스페이스 초대 토큰을 파싱해서 초대 정보를 반환합니다.
     * @param token 파싱할 워크스페이스 초대 토큰
     * @return 초대받은 이메일, 초대한 이메일, 워크스페이스 URL
     * @throws BusinessException 토큰이 유효하지 않거나 워크스페이스 초대 토큰이 아님
     */
    InviteTokenResult parsingInviteToken(String token) throws BusinessException;

    /**
     * 액세스 토큰에서 멤버 이메일을 추출합니다.
     * @param token 이메일을 추출할 액세스 토큰
     * @return 액세스 토큰에 담긴 멤버 이메일
     * @throws BusinessException 토큰이 만료되었거나 액세스 토큰이 아님
     */
    String getEmailFromAccessToken(String token) throws BusinessException;

    /**
     * 리프레쉬 토큰에서 멤버 이메일을 추출합니다.
     * @param token 이메일을 추출할 리프레쉬 토큰
     * @return 리프레쉬 토큰에 담긴 멤버 이메일
     * @throws BusinessException 토큰이 만료되었거나 리프레쉬 토큰이 아님
     */
    String getEmailFromRefreshToken(String token) throws BusinessException;

    /**
     * 이메일 인증 토큰에서 인증 대상 이메일을 추출합니다.
     * @param token 이메일을 추출할 이메일 인증 토큰
     * @return 이메일 인증 토큰에 담긴 인증 대상 이메일
     * @throws BusinessException 토큰이 만료되었거나 이메일 인증 토큰이 아님
     */
    String getEmailFromValidateEmailToken(String token) throws BusinessException;

}
