package com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkspaceMemberJpaRepository extends JpaRepository<WorkspaceMemberEntity, Long> {

    List<WorkspaceMemberEntity> findAllByWorkspaceId(Long workspaceId);

}
