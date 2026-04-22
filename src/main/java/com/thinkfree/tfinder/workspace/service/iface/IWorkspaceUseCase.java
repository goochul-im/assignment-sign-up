package com.thinkfree.tfinder.workspace.service.iface;

public interface IWorkspaceUseCase {

    void inviteMember(String toEmail, Long inviterId, Long workspaceId);

    void acceptMember(String token);

}
