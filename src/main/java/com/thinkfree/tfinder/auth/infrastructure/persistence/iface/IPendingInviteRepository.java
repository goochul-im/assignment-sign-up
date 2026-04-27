package com.thinkfree.tfinder.auth.infrastructure.persistence.iface;

import java.time.Duration;
import java.util.Set;

public interface IPendingInviteRepository {

    /**
     * 회원가입 후 자동 참여시킬 워크스페이스 URL을 저장합니다.
     * @param email 초대를 수락한 이메일
     * @param workspaceUrl 가입 후 참여시킬 워크스페이스 URL
     * @param expiration 참여 대기 정보 유지 시간
     */
    void save(String email, String workspaceUrl, Duration expiration);

    /**
     * 참여 대기중인 워크스페이스 URL 목록을 조회합니다.
     * 참여 대기중인 워크스페이스가 없다면 빈 Set을 반환합니다.
     * @param email 초대를 수락한 이메일
     * @return 해당 이메일이 가입 후 참여해야 하는 워크스페이스 URL 목록
     */
    Set<String> findWorkspaceUrlsByEmail(String email);

    /**
     * 참여 대기중인 워크스페이스 URL 목록을 삭제합니다.
     * @param email 참여 대기 정보를 삭제할 이메일
     */
    void delete(String email);
}
