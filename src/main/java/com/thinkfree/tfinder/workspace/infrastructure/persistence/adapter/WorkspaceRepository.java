package com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.workspace.domain.Workspace;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.iface.IWorkspaceRepository;
import org.springframework.stereotype.Repository;

@Repository
public class WorkspaceRepository implements IWorkspaceRepository {

    @Override
    public Workspace save(Workspace workspace) {
        return null;
    }

    @Override
    public Workspace findById(Long id) {
        return null;
    }

    @Override
    public Workspace findByUrl(String url) {
        return null;
    }
}
