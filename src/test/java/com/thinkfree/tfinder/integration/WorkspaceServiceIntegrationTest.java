package com.thinkfree.tfinder.integration;

import com.thinkfree.tfinder.annotation.IntegrationTest;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IEmailValidateRepository;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IPendingInviteRepository;
import com.thinkfree.tfinder.common.config.JwtProperties;
import com.thinkfree.tfinder.common.util.jwt.iface.IJwtManager;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.domain.IMemberRepository;
import com.thinkfree.tfinder.workspace.domain.IWorkspaceMemberRepository;
import com.thinkfree.tfinder.workspace.domain.IWorkspaceRepository;
import com.thinkfree.tfinder.workspace.domain.MemberEntity;
import com.thinkfree.tfinder.workspace.domain.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberEntity;
import com.thinkfree.tfinder.workspace.service.iface.IWorkspaceUseCase;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@IntegrationTest
@Testcontainers
class WorkspaceServiceIntegrationTest {

    @Autowired
    private IWorkspaceUseCase workspaceUseCase;
    @Autowired
    private IWorkspaceMemberRepository workspaceMemberRepository;
    @Autowired
    private IMemberRepository memberRepository;
    @Autowired
    private IWorkspaceRepository workspaceRepository;
    @Autowired
    private IJwtManager jwtManager;
    @Autowired
    private IEmailValidateRepository emailValidateRepository;
    @Autowired
    private IPendingInviteRepository pendingInviteRepository;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void 워크스페이스를_삭제할_경우_소프트_딜리트가_진행된다(){
        //given
        MemberEntity member = new MemberEntity(
                "testMember",
                "testEmail",
                "testPassword"
        );
        WorkspaceEntity workspace = new WorkspaceEntity(
                "testWorkspace",
                "testUrl"
        );
        WorkspaceMemberEntity workspaceMember = new WorkspaceMemberEntity(
                workspace,
                member,
                WorkspaceMemberRole.OWNER
        );

        memberRepository.save(member);
        workspaceRepository.save(workspace);
        entityManager.flush();
        workspaceMemberRepository.save(workspaceMember);
        entityManager.flush();

        //when
        workspace.delete();
        entityManager.flush();

        //then
        workspace = workspaceRepository.findById(workspace.getId()).get();
        assertThat(workspace.isDelete()).isTrue();
    }

}
