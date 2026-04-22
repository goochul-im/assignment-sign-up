package com.thinkfree.tfinder.workspace.infrastructure.persistence.iface;

import com.thinkfree.tfinder.workspace.domain.Workspace;

public interface IWorkspaceRepository {

    Workspace save(Workspace workspace);

    Workspace findById(Long id);

    Workspace findByUrl(String url);

}
