package com.thinkfree.tfinder.common;

import com.thinkfree.tfinder.common.infrastructure.outbox.iface.IOutboxRepository;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.domain.IMemberRepository;
import com.thinkfree.tfinder.workspace.domain.IWorkspaceMemberRepository;
import com.thinkfree.tfinder.workspace.domain.IWorkspaceRepository;
import com.thinkfree.tfinder.workspace.domain.MemberEntity;
import com.thinkfree.tfinder.workspace.domain.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class DummyDataConstructor {

    private final IMemberRepository memberRepository;
    private final IWorkspaceRepository workspaceRepository;
    private final IWorkspaceMemberRepository workspaceMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final IOutboxRepository outboxRepository;
    private final ObjectMapper mapper;

    @PostConstruct
    void init() {
        MemberEntity member = new MemberEntity("더미 유저", "test@email.com", passwordEncoder.encode("12341234"));
        WorkspaceEntity workspace1 = new WorkspaceEntity("dummy workspace1", "dummy url1");
        WorkspaceEntity workspace2 = new WorkspaceEntity("dummy workspace2", "dummy url2");
        WorkspaceEntity workspace3 = new WorkspaceEntity("dummy workspace3", "dummy url3");
        WorkspaceEntity workspace4 = new WorkspaceEntity("dummy workspace4", "dummy url4");
        WorkspaceEntity workspace5 = new WorkspaceEntity("dummy workspace5", "dummy url5");
        workspace5.delete();

        memberRepository.save(member);

        workspaceRepository.save(workspace1);
        workspaceRepository.save(workspace2);
        workspaceRepository.save(workspace3);
        workspaceRepository.save(workspace4);
        workspaceRepository.save(workspace5);

        WorkspaceMemberEntity workspaceMember1 = new WorkspaceMemberEntity(workspace1, member, WorkspaceMemberRole.OWNER);
        WorkspaceMemberEntity workspaceMember2 = new WorkspaceMemberEntity(workspace2, member, WorkspaceMemberRole.OWNER);
        WorkspaceMemberEntity workspaceMember3 = new WorkspaceMemberEntity(workspace3, member, WorkspaceMemberRole.OWNER);
        WorkspaceMemberEntity workspaceMember4 = new WorkspaceMemberEntity(workspace4, member, WorkspaceMemberRole.OWNER);
        WorkspaceMemberEntity workspaceMember5 = new WorkspaceMemberEntity(workspace5, member, WorkspaceMemberRole.OWNER);
        workspaceMemberRepository.save(workspaceMember1);
        workspaceMemberRepository.save(workspaceMember2);
        workspaceMemberRepository.save(workspaceMember3);
        workspaceMemberRepository.save(workspaceMember4);
        workspaceMemberRepository.save(workspaceMember5);

//        for (int i = 0; i < 100; i++) {
//            outboxRepository.save(new OutboxEntity(
//                    OutboxEventType.JOIN_WORKSPACE_PENDING_INVITE,
//                    mapper.writeValueAsString("hello")
//            ));
//        }

    }

}
