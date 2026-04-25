package com.thinkfree.tfinder.workspace.service.adapter;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.common.service.dto.InviteTokenResult;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.infrastructure.external.iface.IMailSender;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter.IWorkspaceRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter.IWorkspaceMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
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
    public void inviteMember(String toEmail, long inviterId, long workspaceId) {

        MemberEntity inviter = memberRepository.findById(inviterId).orElseThrow(
                () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
        );
        WorkspaceEntity inviteWorkspace = workspaceRepository.findById(workspaceId).orElseThrow(
                () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
        );

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
        if (memberRepository.existsByEmail(toMail)) {
            // 이미 회원일 경우
            String workspaceUrl = result.workspaceUrl();
            MemberEntity member = memberRepository.findByEmail(toMail).orElseThrow(
                    () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
            );
            WorkspaceEntity workspace = workspaceRepository.findByWorkspaceUrl(workspaceUrl).orElseThrow(
                    () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
            );

            if (workspaceMemberRepository.existsByWorkspaceAndMember(workspace, member)){
                throw new BusinessException(ErrorCode.DUPLICATE_WORKSPACE_MEMBER);
            }

            WorkspaceMemberEntity workspaceMember = new WorkspaceMemberEntity(
                    workspace,
                    member,
                    WorkspaceMemberRole.MEMBER,
                    Instant.now()
            );

            workspaceMemberRepository.save(workspaceMember);
        } else {
            // 회원이 아닐 경우
            throw new BusinessException(ErrorCode.SIGNUP_FIRST);
        }

    }

    private String makeInviteMailMessage(MemberEntity member, String token) {
        // 들어가야 할 정보
        // 클릭할 URL + 메시지 내용
        String inviteUrl = FRONTEND_URL + "?token=" + token;

        StringBuilder sb = new StringBuilder()
                .append("<h2>tfinder 워크스페이스 초대</h2>")
                .append("<p>tfinder에서 초대가 왔습니다.</p>")
                .append("<p>아래 링크를 눌러 참가하세요.</p>")
                .append("<p>")
                .append("<a href=\"")
                .append(inviteUrl)
                .append("\">참가하기</a>")
                .append("</p>")
                .append("<p>링크가 열리지 않는다면 아래 주소를 복사해서 브라우저에 붙여넣어 주세요.</p>")
                .append("<p>")
                .append(inviteUrl)
                .append("</p>");

        return sb.toString();
    }

}
