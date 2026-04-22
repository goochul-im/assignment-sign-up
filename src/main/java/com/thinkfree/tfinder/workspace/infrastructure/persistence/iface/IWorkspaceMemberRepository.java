package com.thinkfree.tfinder.workspace.infrastructure.persistence.iface;

import com.thinkfree.tfinder.workspace.domain.WorkspaceMember;

import java.util.List;

public interface IWorkspaceMemberRepository {

    WorkspaceMember save(WorkspaceMember workspaceMember);

    WorkspaceMember findById(Long workspaceMemberId);

    List<WorkspaceMember> findByWorkspaceId(Long workspaceId);

}
