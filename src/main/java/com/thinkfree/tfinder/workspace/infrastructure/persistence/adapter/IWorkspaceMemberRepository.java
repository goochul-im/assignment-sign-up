package com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
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
     * 이 멤버가 속한 워크스페이스를 반환
     * @param member 워크스페이스들을 찾을 멤버
     * @return 해당 멤버가 속해있는 워크스페이스 리스트, 없을 경우 empty list 반환
     */
    @Query("""
            select workspaceMember
            from workspace_member workspaceMember
            join fetch workspaceMember.workspace
            where workspaceMember.member = :member
            """)
    List<WorkspaceMemberEntity> findAllWorkspaceByMember(MemberEntity member);

    /**
     * 워크스페이스에 속한 멤버를 반환
     * @param workspace 멤버들을 찾을 워크스페이스
     * @return 해당 워크스페이스에 속해있는 멤버 리스트, 없을 경우 empty list 반환
     */
    @Query("""
            select workspaceMember
            from workspace_member workspaceMember
            join fetch workspaceMember.member
            where workspaceMember.workspace = :workspace
            """)
    List<WorkspaceMemberEntity> findAllMemberByWorkspace(WorkspaceEntity workspace);

}
