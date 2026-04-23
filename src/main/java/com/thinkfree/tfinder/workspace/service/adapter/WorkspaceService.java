package com.thinkfree.tfinder.workspace.service.adapter;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import com.thinkfree.tfinder.workspace.domain.Member;
import com.thinkfree.tfinder.workspace.domain.Workspace;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMember;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.infrastructure.external.iface.IMailSender;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.iface.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.iface.IWorkspaceMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.iface.IWorkspaceRepository;
import com.thinkfree.tfinder.common.service.dto.InviteTokenResult;
import com.thinkfree.tfinder.workspace.service.iface.IWorkspaceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WorkspaceService implements IWorkspaceUseCase {

    private final IWorkspaceRepository workspaceRepository;
    private final IMemberRepository memberRepository;
    private final IWorkspaceMemberRepository workspaceMemberRepository;
    private final IMailSender mailSender;
    private final IJwtManager jwtManager;

    private final Long inviteTokenExpirationTime = (long) (2 * 24 * 60 * 60);

    @Override
    public void inviteMember(String toEmail, Long inviterId, Long workspaceId) {

        Member inviter = memberRepository.findById(inviterId);
        Workspace inviteWorkspace = workspaceRepository.findById(workspaceId);

        String inviteToken = jwtManager.generateInviteToken(
                inviter.getEmail(),
                toEmail,
                inviteWorkspace.getWorkspaceUrl(),
                Instant.now().plusSeconds(inviteTokenExpirationTime));

        String subject = ""; //TODO: subject 넣기
        mailSender.asyncSend(
                toEmail,
                subject,
                makeInviteMailMessage(inviteToken)
                );

    }

    @Override
    public void acceptMember(String token) {

        InviteTokenResult result = jwtManager.parsingInviteToken(token);
        String toMail = result.toEmail();
        if (memberRepository.isExistByEmail(toMail)) {
            // 이미 회원일 경우
            String workspaceUrl = result.workspaceUrl();
            Member member = memberRepository.findByEmail(toMail);
            Workspace workspace = workspaceRepository.findByUrl(workspaceUrl);

            WorkspaceMember workspaceMember = new WorkspaceMember(
                    member.getId(),
                    workspace.getId(),
                    WorkspaceMemberRole.MEMBER,
                    Instant.now()
            );

            workspaceMemberRepository.save(workspaceMember);
        } else {
            // 회원이 아닐 경우
            throw new BusinessException(ErrorCode.SIGNUP_FIRST);
        }

    }

    private String makeInviteMailMessage(String token) {
        // 들어가야 할 정보
        // 클릭할 URL + 메시지 내용
        return "";
    }

}
