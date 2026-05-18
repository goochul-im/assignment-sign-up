package com.thinkfree.tfinder.common.infrastructure.outbox.handler;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IPendingInviteRepository;
import com.thinkfree.tfinder.common.infrastructure.outbox.OutboxEventEntity;
import com.thinkfree.tfinder.common.infrastructure.outbox.iface.IOutboxEventHandler;
import com.thinkfree.tfinder.common.infrastructure.outbox.enumrate.OutboxEventType;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class JoinPendingInviteHandler implements IOutboxEventHandler {

    private final IPendingInviteRepository pendingInviteRepository;
    private final IMemberRepository memberRepository;
    private final IWorkspaceRepository workspaceRepository;
    private final IWorkspaceMemberRepository workspaceMemberRepository;
    private final JoinPendingInviteOutboxMapper mapper;


    @Override
    public OutboxEventType supportType() {
        return OutboxEventType.JOIN_WORKSPACE_PENDING_INVITE;
    }

    @Override
    public void handle(OutboxEventEntity outbox) {
        JoinPendingInvitePayload payload = mapper.fromPayload(outbox.getPayload());
        MemberEntity member = memberRepository.findById(payload.memberId()).orElseThrow(
                RuntimeException::new
        );

        handle(member);
        if (pendingInviteRepository.isRemain(member.getEmail())) {
            throw new RuntimeException("가입 완료되지 않은 워크스페이스 존재");
        }
    }

    /**
     * 대기중이던 초대 수락 이벤트들을 소모해
     * 회원을 워크스페이스에 가입시킵니다.
     */
    private void handle(MemberEntity member) {

        String email = member.getEmail();
        Set<String> pendingWorkspaceUrls = pendingInviteRepository.findWorkspaceUrlsByEmail(email);
        // redis에서 가져오는 걸 실패할떄는 어떻게 하지??
        // 아... 이거 쿼리가 너무 많이 나갈수 있겠는데... 나중에 bulk insert로 바꿔야 하나?

        for (String workspaceUrl : pendingWorkspaceUrls) {
            try {
                if (workspaceUrl.equals("dummy url4")) {
                    throw new Exception();
                }

                WorkspaceEntity workspace = workspaceRepository.findByWorkspaceUrl(workspaceUrl).orElseThrow(
                        Exception::new
                );

                if (!workspaceMemberRepository.existsByWorkspaceAndMember(workspace, member)) {
                    workspaceMemberRepository.save(new WorkspaceMemberEntity(
                            workspace,
                            member,
                            WorkspaceMemberRole.MEMBER
                    ));
                }
                pendingInviteRepository.deleteOne(email, workspaceUrl);
            } catch (DataIntegrityViolationException e) {
                log.warn("워크스페이스 중복 참여 시도 발생, member = {}, workspaceUrl = {}", member.getEmail(), workspaceUrl);
                pendingInviteRepository.deleteOne(email, workspaceUrl);
            } catch (Exception e) {
                log.warn("참여 대기중인 워크스페이스에 참여 중 에러 발생, member = {}, workspaceUrl = {}", member.getEmail(), workspaceUrl);
            }
        }
    }

}
