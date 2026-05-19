package com.thinkfree.tfinder.workspace.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IMemberRepository extends JpaRepository<MemberEntity, Long> {

    boolean existsByEmail(String email);

    Optional<MemberEntity> findByEmail(String email);

    /**
     * 워크스페이스에 속한 멤버들의 이메일 중 emails에 없는 이메일만 Set으로 가져옵니다.
     * @param workspace 찾을 워크스페이스
     * @param emails 찾을 이메일 리스트
     * @return 이메일 리스트 중 워크스페이스에 속한 멤버의 이메일 Set
     */
    @Query(value = """
        select m.email
        from MemberEntity m
        join WorkspaceMemberEntity wm on m = wm.member
        where wm.workspace = :workspace
        and m.email in :emails
        """)
    Set<String> findJoinedEmails(WorkspaceEntity workspace, List<String> emails);

}
