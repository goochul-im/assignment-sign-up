package com.thinkfree.tfinder.workspace.infrastructure.persistence;

import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IWorkspaceRepository extends JpaRepository<WorkspaceEntity, Long> {

    Optional<WorkspaceEntity> findByWorkspaceUrl(String workspaceUrl);

    boolean existsByWorkspaceName(String workspaceName);

    boolean existsByWorkspaceUrl(String workspaceUrl);

}
