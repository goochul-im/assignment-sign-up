package com.thinkfree.tfinder.workspace.service.iface;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.workspace.service.dto.MyWorkspacesResultDto;
import com.thinkfree.tfinder.workspace.service.dto.WorkspaceMemberResultDto;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.service.dto.CreateWorkspaceDto;

import java.util.List;

public interface IWorkspaceUseCase {

    /**
     * 워크스페이스를 생성합니다.
     * @param dto 워크스페이스 생성 요청 DTO
     * @return 생성된 워크스페이스
     * @throws BusinessException 이름이 중복되거나, URL이 중복됨
     */
    WorkspaceEntity create(CreateWorkspaceDto dto) throws BusinessException;

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

    /**
     * 멤버를 워크스페이스에 초대합니다.
     * @param toEmailList 수신자들
     * @param inviterId 초대한 멤버의 ID
     * @param workspaceId 초대한 워크스페이스 ID
     * @throws BusinessException
     * 가입되어있지 않은 멤버
     * 존재하지 않는 워크스페이스
     * 워크스페이스의 관리자나 소유자가 아님
     * 워크스페이스에 속해있지 않음
     */
    void inviteMember(List<String> toEmailList, long inviterId, long workspaceId) throws BusinessException;

    /**
     * 초대를 수락할 때 사용됩니다. 내부적으로 토큰을 파싱해서 워크스페이스에 초대를 수락한 멤버를 추가해야합니다
     * @param token 초대 토큰
     */
    void acceptMember(String token) throws BusinessException;

}
