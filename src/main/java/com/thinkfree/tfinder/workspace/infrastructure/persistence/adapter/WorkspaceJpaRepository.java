package com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkspaceJpaRepository extends JpaRepository<WorkspaceEntity, Long> {

    Optional<WorkspaceEntity> findByWorkspaceUrl(String workspaceUrl);

}
