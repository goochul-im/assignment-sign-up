package com.thinkfree.tfinder.workspace.service.iface;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.workspace.service.dto.MyWorkspaceResponse;
import com.thinkfree.tfinder.workspace.service.dto.WorkspaceMembersPageResponse;
import com.thinkfree.tfinder.workspace.service.dto.getWorkspaceMembersPageCommand;

public interface IWorkspaceQuery {

    /**
     * 멤버가 속한 모든 워크스페이스를 조회합니다.
     * @param memberId 조회 요청 멤버 ID
     * @return 멤버가 속한 워크스페이스 목록
     * @throws BusinessException 요청자가 존재하지 않음
     */
    MyWorkspaceResponse getMyWorkspaces(long memberId) throws BusinessException;

    /**
     * 워크스페이스에 속한 멤버를 페이지로 조회합니다.
     * @param command 조회 요청 dto
     * @return 워크스페이스에 속한 멤버 목록 페이지
     * @throws BusinessException 요청자 또는 워크스페이스가 존재하지 않거나, 요청자가 워크스페이스에 속해있지 않음
     */
    WorkspaceMembersPageResponse getWorkspaceMembersPage(getWorkspaceMembersPageCommand command) throws BusinessException;

}
