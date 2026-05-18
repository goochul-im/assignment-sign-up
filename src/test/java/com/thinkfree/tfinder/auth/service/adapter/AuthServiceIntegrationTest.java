package com.thinkfree.tfinder.auth.service.adapter;

import com.thinkfree.tfinder.annotation.IntegrationTest;
import com.thinkfree.tfinder.auth.controller.request.SignupRequest;
import com.thinkfree.tfinder.auth.service.iface.IAuthUseCase;
import com.thinkfree.tfinder.common.exception.SignupRequireException;
import com.thinkfree.tfinder.common.infrastructure.outbox.OutboxEventEntity;
import com.thinkfree.tfinder.common.infrastructure.outbox.enumrate.OutboxEventStatus;
import com.thinkfree.tfinder.common.infrastructure.outbox.enumrate.OutboxEventType;
import com.thinkfree.tfinder.common.infrastructure.outbox.iface.IOutboxRepository;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import com.thinkfree.tfinder.workspace.service.iface.IWorkspaceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.function.BooleanSupplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@IntegrationTest
@Testcontainers
class AuthServiceIntegrationTest {

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private IAuthUseCase authUseCase;
    @Autowired
    private IWorkspaceUseCase workspaceUseCase;
    @Autowired
    private IJwtManager jwtManager;
    @Autowired
    private IMemberRepository memberRepository;
    @Autowired
    private IWorkspaceRepository workspaceRepository;
    @Autowired
    private IWorkspaceMemberRepository workspaceMemberRepository;
    @Autowired
    private IOutboxRepository outboxRepository;

    @Test
    void 회원가입_후_아웃박스_스케줄링으로_대기중인_초대를_수락한다() throws InterruptedException {
        // given
        String ownerEmail = "owner@test.com";
        String signupEmail = "invitee@test.com";
        String workspaceUrl = "test-workspace";

        MemberEntity owner = memberRepository.save(new MemberEntity(
                "owner",
                ownerEmail,
                "password"
        ));
        WorkspaceEntity workspace = workspaceRepository.save(new WorkspaceEntity(
                "test-workspace",
                workspaceUrl
        ));
        workspaceMemberRepository.save(new WorkspaceMemberEntity(
                workspace,
                owner,
                WorkspaceMemberRole.OWNER
        ));

        String inviteToken = jwtManager.generateInviteToken(ownerEmail, signupEmail, workspaceUrl);
        assertThrows(
                SignupRequireException.class,
                () -> workspaceUseCase.acceptInvite(inviteToken)
        );

        // when
        authUseCase.signUp(new SignupRequest(
                "invitee",
                signupEmail,
                "password"
        ));

        // then
        MemberEntity signupMember = memberRepository.findByEmail(signupEmail).orElseThrow();
        awaitUntil(() -> workspaceMemberRepository.existsByWorkspaceAndMember(workspace, signupMember));

        assertThat(workspaceMemberRepository.existsByWorkspaceAndMember(workspace, signupMember)).isTrue();
        assertThat(outboxRepository.findAll())
                .filteredOn(outbox -> outbox.getEventType() == OutboxEventType.JOIN_WORKSPACE_PENDING_INVITE)
                .extracting(OutboxEventEntity::getStatus)
                .contains(OutboxEventStatus.DONE);
    }

    private void awaitUntil(BooleanSupplier condition) throws InterruptedException {
        long waitTime = System.currentTimeMillis() + Duration.ofSeconds(10).toMillis(); // 10초만 기다림

        while (System.currentTimeMillis() < waitTime) {
            if (condition.getAsBoolean()) {
                return;
            }
            Thread.sleep(200);
        }
    }
}
