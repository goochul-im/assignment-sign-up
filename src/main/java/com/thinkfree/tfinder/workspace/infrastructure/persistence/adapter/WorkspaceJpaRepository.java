package com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.workspace.domain.Workspace;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkspaceJpaRepository extends JpaRepository<WorkspaceEntity, Long> {

    Optional<WorkspaceEntity> findByUrl(String url);

}
