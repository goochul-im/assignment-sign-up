package com.thinkfree.tfinder.workspace.service.iface;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.workspace.service.dto.MyWorkspacesResultDto;
import com.thinkfree.tfinder.workspace.service.dto.WorkspaceMemberResultDto;

import java.util.List;

public interface IWorkspaceQuery {

    /**
     * 멤버가 속한 모든 워크스페이스를 조회합니다.
     * @param requesterId 조회 요청 멤버 ID
     * @return 멤버가 속한 워크스페이스 목록
     * @throws BusinessException 요청자가 존재하지 않음
     */
    List<MyWorkspacesResultDto> findMyWorkspaces(long requesterId) throws BusinessException;

    /**
     * 워크스페이스에 속한 모든 멤버를 조회합니다.
     * @param requesterId 조회를 요청한 멤버 ID
     * @param workspaceId 조회할 워크스페이스 ID
     * @return 워크스페이스에 속한 멤버 목록
     * @throws BusinessException 요청자 또는 워크스페이스가 존재하지 않거나, 요청자가 워크스페이스에 속해있지 않음
     */
    List<WorkspaceMemberResultDto> findWorkspaceMembers(long requesterId, long workspaceId) throws BusinessException;

}
