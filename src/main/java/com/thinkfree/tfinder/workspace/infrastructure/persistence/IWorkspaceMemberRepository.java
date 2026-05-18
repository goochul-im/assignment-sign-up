package com.thinkfree.tfinder.workspace.infrastructure.persistence;

import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IWorkspaceMemberRepository extends JpaRepository<WorkspaceMemberEntity, Long> {

    /**
     * 워크스페이스에 속해있는 멤버를 가져옴
     * @param workspace 멤버들을 찾고 싶은 워크스페이스
     * @return 해당 워크스페이스에 속해있는 멤버 리스트. 없을 경우 empty list 반환
     */
    List<WorkspaceMemberEntity> findAllByWorkspace(WorkspaceEntity workspace);

    boolean existsByWorkspaceAndMember(WorkspaceEntity workspace, MemberEntity member);

    Optional<WorkspaceMemberEntity> findByWorkspaceAndMember(WorkspaceEntity workspace, MemberEntity member);

    /**
     * 이 멤버가 속한 워크스페이스에 대한 워크스페이스 멤버 반환
     * @param member 워크스페이스들을 찾을 멤버
     * @return 해당 멤버가 속해있는 워크스페이스에 대한 워크스페이스 멤버 리스트, 없을 경우 empty list 반환
     */
    @Query("""
            select workspaceMember
            from WorkspaceMemberEntity workspaceMember
            join fetch workspaceMember.workspace
            where workspaceMember.member = :member
            """)
    List<WorkspaceMemberEntity> findAllByMember(MemberEntity member);

    /**
     * 워크스페이스에 속한 멤버를 페이지로 반환
     * @param workspace 멤버들을 찾을 워크스페이스
     * @param pageable 페이지 정보
     * @return 해당 워크스페이스에 속해있는 멤버 리스트, 없을 경우 empty list 반환
     */
    @Query("""
            select workspaceMember
            from WorkspaceMemberEntity workspaceMember
            join fetch workspaceMember.member
            where workspaceMember.workspace = :workspace
            """)
    Page<WorkspaceMemberEntity> findWorkspaceMemberPage(WorkspaceEntity workspace, Pageable pageable);

    /**
     * 워크스페이스에 해당 이메일로 가입된 멤버가 있는지 확인
     * @param workspace 멤버를 찾을 워크스페이스
     * @param email 존재하는지 확인할 이메일
     * @return 해당 이메일을 가진 멤버가 존재하면 true, 아닐 경우 false
     */
    @Query("""
        select case when (count(*) > 0) then true else false end
        from WorkspaceMemberEntity wm
        join MemberEntity m on wm.member.id = m.id
        where m.email = :email
        """)
    boolean existsByWorkspaceAndMemberEmail(WorkspaceEntity workspace, String email);

    void deleteAllByWorkspace(WorkspaceEntity workspace);

    int countAllByWorkspace(WorkspaceEntity workspace);

}
