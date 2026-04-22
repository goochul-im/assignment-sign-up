package com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMember;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.iface.IWorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WorkspaceMemberRepository implements IWorkspaceMemberRepository {

    private final WorkspaceMemberJpaRepository workspaceMemberJpaRepository;

    @Override
    public WorkspaceMember save(WorkspaceMember workspaceMember) {
        return workspaceMemberJpaRepository.save(WorkspaceMemberEntity.fromDomain(workspaceMember)).toDomain();
    }

    @Override
    public WorkspaceMember findById(Long workspaceMemberId) {
        return workspaceMemberJpaRepository.findById(workspaceMemberId).orElseThrow(
                () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
        ).toDomain();
    }

    @Override
    public List<WorkspaceMember> findByWorkspaceId(Long workspaceId) {
        return workspaceMemberJpaRepository.findAllByWorkspaceId(workspaceId).stream().map(
                WorkspaceMemberEntity::toDomain
        ).toList();
    }
}
