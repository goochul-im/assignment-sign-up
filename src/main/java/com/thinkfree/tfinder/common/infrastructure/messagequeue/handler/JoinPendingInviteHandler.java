package com.thinkfree.tfinder.common.infrastructure.messagequeue.handler;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IPendingInviteRepository;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class JoinPendingInviteHandler {

    private final IPendingInviteRepository pendingInviteRepository;
    private final IWorkspaceRepository workspaceRepository;
    private final IWorkspaceMemberRepository workspaceMemberRepository;

    public void handle(MemberEntity member) {

        String email = member.getEmail();
        Set<String> pendingWorkspaceUrls = pendingInviteRepository.findWorkspaceUrlsByEmail(email);
        // redis에서 가져오는 걸 실패할떄는 어떻게 하지??
        // 아... 이거 쿼리가 너무 많이 나갈수 있겠는데... 나중에 bulk insert로 바꿔야 하나?

        for (String workspaceUrl : pendingWorkspaceUrls) {
            try {
                WorkspaceEntity workspace = workspaceRepository.findByWorkspaceUrl(workspaceUrl).orElseThrow(
                        RuntimeException::new
                );

                //TODO: bulk insert 같은걸로 바꿀수는 없나?
                if (!workspaceMemberRepository.existsByWorkspaceAndMember(workspace, member)) {
                    workspaceMemberRepository.save(new WorkspaceMemberEntity(
                            workspace,
                            member,
                            WorkspaceMemberRole.MEMBER
                    ));
                }
            } catch (Exception e) {
                log.warn("참여 대기중인 워크스페이스에 참여 중 에러 발생, member = {}, workspaceUrl = {}", member.getEmail(), workspaceUrl);
            }
        }

        String signupEmail = member.getEmail();
        try {
            if (!pendingInviteRepository.delete(signupEmail)) {
                // 여기서도 레디스 커넥션 fail이 발생하면, 전부다 롤백되는가?
                log.warn("워크스페이스 대기 정보 삭제 실패, email = {}",signupEmail);
            }
        } catch (IllegalArgumentException e) {
            log.warn("null값 삭제 시도로 인한 예외 발생");
        }
    }

}
