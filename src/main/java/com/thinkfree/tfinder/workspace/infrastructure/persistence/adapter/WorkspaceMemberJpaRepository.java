package com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkspaceMemberJpaRepository extends JpaRepository<WorkspaceMemberEntity, Long> {

    List<WorkspaceMemberEntity> findAllByWorkspaceId(Long workspaceId);

}
