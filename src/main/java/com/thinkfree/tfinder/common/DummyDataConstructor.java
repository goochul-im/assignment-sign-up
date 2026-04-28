package com.thinkfree.tfinder.common;

import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DummyDataConstructor {

    private final IMemberRepository memberRepository;
    private final IWorkspaceRepository workspaceRepository;
    private final IWorkspaceMemberRepository workspaceMemberRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    void init() {
        MemberEntity member = new MemberEntity("더미 유저", "test@email.com", passwordEncoder.encode("12341234"));
        MemberEntity member2 = new MemberEntity("더미 유저2", "goochul175465@gmail.com", passwordEncoder.encode("12341234"));
        WorkspaceEntity workspace1 = new WorkspaceEntity("tfinder workspace", "tfinder url");
        WorkspaceEntity workspace2 = new WorkspaceEntity("티파인더 workspace", "tfinder2 url");
        WorkspaceEntity workspace3 = new WorkspaceEntity("뉴파인더 workspace", "newfinder url");
        WorkspaceEntity workspace4 = new WorkspaceEntity("더파인더 workspace", "thefinder url");

        memberRepository.save(member);
        memberRepository.save(member2);

        workspaceRepository.save(workspace1);
        workspaceRepository.save(workspace2);
        workspaceRepository.save(workspace3);
        workspaceRepository.save(workspace4);

        WorkspaceMemberEntity workspaceMember1 = new WorkspaceMemberEntity(workspace1, member, WorkspaceMemberRole.OWNER);
        WorkspaceMemberEntity workspaceMember2 = new WorkspaceMemberEntity(workspace2, member, WorkspaceMemberRole.OWNER);
        WorkspaceMemberEntity workspaceMember3 = new WorkspaceMemberEntity(workspace3, member, WorkspaceMemberRole.OWNER);
        WorkspaceMemberEntity workspaceMember4 = new WorkspaceMemberEntity(workspace4, member, WorkspaceMemberRole.OWNER);
        workspaceMemberRepository.save(workspaceMember1);
        workspaceMemberRepository.save(workspaceMember2);
        workspaceMemberRepository.save(workspaceMember3);
        workspaceMemberRepository.save(workspaceMember4);
    }

}
