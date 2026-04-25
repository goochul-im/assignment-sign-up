package com.thinkfree.tfinder.workspace.service.iface;

import com.thinkfree.tfinder.common.exception.BusinessException;

public interface IWorkspaceUseCase {

    /**
     * 멤버를 초대할 때 사용됩니다
     * @param toEmail 수신자
     * @param inviterId 초대한 멤버의 ID
     * @param workspaceId 초대한 워크스페이스 ID
     */
    void inviteMember(String toEmail, long inviterId, long workspaceId) throws BusinessException;

    /**
     * 초대를 수락할 때 사용됩니다. 내부적으로 토큰을 파싱해서 워크스페이스에 초대를 수락한 멤버를 추가해야합니다
     * @param token 초대 토큰
     */
    void acceptMember(String token) throws BusinessException;

}
