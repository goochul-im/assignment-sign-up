package com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.workspace.domain.Workspace;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.iface.IWorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WorkspaceRepository implements IWorkspaceRepository {

    private final WorkspaceJpaRepository workspaceJpaRepository;

    @Override
    public Workspace save(Workspace workspace) {
        return workspaceJpaRepository.save(WorkspaceEntity.fromDomain(workspace)).toDomain();
    }

    @Override
    public Workspace findById(Long id) {
        return workspaceJpaRepository.findById(id).orElseThrow(
                () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
        ).toDomain();
    }

    @Override
    public Workspace findByUrl(String url) {
        return workspaceJpaRepository.findByUrl(url).orElseThrow(
                () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
        ).toDomain();
    }
}
