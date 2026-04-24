package com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IWorkspaceMemberRepository extends JpaRepository<WorkspaceMemberEntity, Long> {

    // TODO: 페이징을 해야하나? 하나의 워크스페이스에 엄청나게 많은 멤버가 있으면?
    /**
     * @param workspace 멤버들을 찾고 싶은 워크스페이스
     * @return 해당 워크스페이스에 속해있는 멤버 리스트.
     */
    List<WorkspaceMemberEntity> findAllByWorkspace(WorkspaceEntity workspace);

}
