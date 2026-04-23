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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WorkspaceService implements IWorkspaceUseCase {

    private final IWorkspaceMemberRepository workspaceMemberRepository;
    private final IMemberRepository memberRepository;
    private final IWorkspaceRepository workspaceRepository;
    private final IMailSender mailSender;
    private final IJwtManager jwtManager;

    private final long inviteTokenExpirationTime = 2 * 24 * 60 * 60;

    @Value("${frontend.url}")
    private String FRONTEND_URL;

    @Override
    public void inviteMember(String toEmail, Long inviterId, Long workspaceId) {

        Member inviter = memberRepository.findById(inviterId);
        Workspace inviteWorkspace = workspaceRepository.findById(workspaceId);

        String inviteToken = jwtManager.generateInviteToken(
                inviter.getEmail(),
                toEmail,
                inviteWorkspace.getWorkspaceUrl(),
                Instant.now().plusSeconds(inviteTokenExpirationTime));

        String subject = "invite token"; // TODO: 이걸 상수로 뺄까? 아니면 환경변수로 뺄까?
        mailSender.asyncSend(
                toEmail,
                subject,
                makeInviteMailMessage(inviter, inviteToken)
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

    private String makeInviteMailMessage(Member member, String token) {
        // 들어가야 할 정보
        // 클릭할 URL + 메시지 내용
        StringBuilder sb = new StringBuilder()
                .append("<p>tfinder에서 초대가 왔습니다. 다음 링크를 눌러 참가하세요</p>")
                .append("<p><a href=" + FRONTEND_URL + ">참가하기</a></p>");
        return sb.toString();
    }

}
