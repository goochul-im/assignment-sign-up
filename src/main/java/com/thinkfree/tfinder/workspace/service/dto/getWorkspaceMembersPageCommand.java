package com.thinkfree.tfinder.workspace.service.dto;

/**
 * 워크스페이스에 속한 멤버를 페이지로 조회 요청합니다.
 * @param requesterId 조회를 요청한 멤버 ID
 * @param workspaceId 조회할 워크스페이스 ID
 * @param page 페이지 번호
 * @param pageSize 페이지 크기
 * @return 워크스페이스에 속한 멤버 목록 페이지
 */
public record getWorkspaceMembersPageCommand(
        long requesterId,
        long workspaceId,
        int page,
        int pageSize
) {
}
