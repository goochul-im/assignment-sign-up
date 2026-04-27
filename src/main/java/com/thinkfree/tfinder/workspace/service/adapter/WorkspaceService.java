package com.thinkfree.tfinder.workspace.service.adapter;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IEmailValidateRepository;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IPendingInviteRepository;
import com.thinkfree.tfinder.common.config.JwtProperties;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.common.service.dto.InviteTokenResult;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.infrastructure.external.iface.IMailSender;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import com.thinkfree.tfinder.workspace.service.dto.MyWorkspacesResultDto;
import com.thinkfree.tfinder.workspace.service.dto.CreateWorkspaceDto;
import com.thinkfree.tfinder.workspace.service.dto.WorkspaceMemberResultDto;
import com.thinkfree.tfinder.workspace.service.iface.IWorkspaceQuery;
import com.thinkfree.tfinder.workspace.service.iface.IWorkspaceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkspaceService implements IWorkspaceUseCase, IWorkspaceQuery {

    private final IWorkspaceMemberRepository workspaceMemberRepository;
    private final IMemberRepository memberRepository;
    private final IWorkspaceRepository workspaceRepository;
    private final IMailSender mailSender;
    private final IJwtManager jwtManager;
    private final IEmailValidateRepository emailValidateRepository;
    private final IPendingInviteRepository pendingInviteRepository;
    private final JwtProperties jwtProperties;

    @Value("${frontend.url}")
    private String FRONTEND_URL;

    @Override
    public WorkspaceEntity create(CreateWorkspaceDto dto) throws BusinessException {
        MemberEntity creator = getMemberOrThrowE001(memberRepository.findById(dto.requestMemberId()));

        if (workspaceRepository.existsByWorkspaceName(dto.workspaceName()) || workspaceRepository.existsByWorkspaceUrl(dto.workspaceUrl())) {
            throw new BusinessException(ErrorCode.DUPLICATE_ERROR);
        }

        WorkspaceEntity workspace = new WorkspaceEntity(
                dto.workspaceName(),
                dto.workspaceUrl()
        );
        workspaceRepository.save(workspace);

        WorkspaceMemberEntity workspaceMember = new WorkspaceMemberEntity(
                workspace,
                creator,
                WorkspaceMemberRole.OWNER
        );
        workspaceMemberRepository.save(workspaceMember);

        return workspace;
    }

    @Override
    public List<MyWorkspacesResultDto> findMyWorkspaces(long requesterId) throws BusinessException {
        MemberEntity member = getMemberOrThrowE001(memberRepository.findById(requesterId));

        return workspaceMemberRepository.findAllWorkspaceByMember(member)
                .stream()
                .map(workspaceMember -> {
                    WorkspaceEntity workspace = workspaceMember.getWorkspace();
                    return new MyWorkspacesResultDto(
                            workspace.getId(),
                            workspace.getWorkspaceName(),
                            workspace.getWorkspaceUrl(),
                            workspaceMember.getRole()
                    );
                })
                .toList();
    }

    @Override
    public List<WorkspaceMemberResultDto> findWorkspaceMembers(long requesterId, long workspaceId) throws BusinessException {
        MemberEntity requester = getMemberOrThrowE001(memberRepository.findById(requesterId));
        WorkspaceEntity workspace = getWorkspaceOrThrowE001(workspaceRepository.findById(workspaceId));

        getWorkspaceMemberOrThrowA002(workspace, requester);

        return workspaceMemberRepository.findAllMemberByWorkspace(workspace)
                .stream()
                .map(workspaceMember -> {
                    MemberEntity member = workspaceMember.getMember();

                    return new WorkspaceMemberResultDto(
                            member.getId(),
                            member.getNickname(),
                            member.getEmail(),
                            workspaceMember.getRole()
                    );
                })
                .toList();
    }

    @Override
    public void inviteMember(List<String> toEmailList, long inviterId, long workspaceId) throws BusinessException{

        if (toEmailList.size() > 50) {
            throw new BusinessException(ErrorCode.TOO_MANY_INVITE);
        }

        MemberEntity inviter = getMemberOrThrowE001(memberRepository.findById(inviterId));

        WorkspaceEntity inviteWorkspace = getWorkspaceOrThrowE001(workspaceRepository.findById(workspaceId));

        WorkspaceMemberEntity workspaceMember = getWorkspaceMemberOrThrowA002(inviteWorkspace, inviter);

        WorkspaceMemberRole role = workspaceMember.getRole();
        if (!(role == WorkspaceMemberRole.MANAGER || role == WorkspaceMemberRole.OWNER)) {
            throw new BusinessException(ErrorCode.AUTHORIZATION_FAILED);
        }

        for (String toEmail : toEmailList) {
            String inviteToken = jwtManager.generateInviteToken(
                    inviter.getEmail(),
                    toEmail,
                    inviteWorkspace.getWorkspaceUrl(),
                    Instant.now().plusSeconds(jwtProperties.getInviteExpirationSeconds())
            );

            String subject = "invite token";
            mailSender.asyncSend(
                    toEmail,
                    subject,
                    makeInviteMailMessage(inviteWorkspace, inviteToken)
            );
        }

    }

    @Override
    public void acceptInvite(String token) throws BusinessException{

        InviteTokenResult result = jwtManager.parsingInviteToken(token);
        String toMail = result.toEmail();
        if (memberRepository.existsByEmail(toMail)) {
            // 이미 회원일 경우
            String workspaceUrl = result.workspaceUrl();
            MemberEntity member = getMemberOrThrowE001(memberRepository.findByEmail(toMail));
            WorkspaceEntity workspace = getWorkspaceOrThrowE001(workspaceRepository.findByWorkspaceUrl(workspaceUrl));

            if (workspaceMemberRepository.existsByWorkspaceAndMember(workspace, member)){
                throw new BusinessException(ErrorCode.DUPLICATE_ERROR);
            }

            WorkspaceMemberEntity workspaceMember = new WorkspaceMemberEntity(
                    workspace,
                    member,
                    WorkspaceMemberRole.MEMBER
            );

            workspaceMemberRepository.save(workspaceMember);
        } else {
            // 회원이 아닐 경우
            Duration expiration = Duration.ofSeconds(jwtProperties.getValidateEmailExpirationSeconds());
            emailValidateRepository.save(result.toEmail(), expiration);
            pendingInviteRepository.save(result.toEmail(), result.workspaceUrl(), expiration);
            throw new BusinessException(ErrorCode.SIGNUP_FIRST);
        }

    }

    private String makeInviteMailMessage(WorkspaceEntity workspace, String token) {
        // 들어가야 할 정보
        // 클릭할 URL + 메시지 내용
        String inviteUrl = FRONTEND_URL + "?token=" + token;

        StringBuilder sb = new StringBuilder()
                .append("<h2>tfinder 워크스페이스 초대</h2>")
                .append("<p><b>")
                .append(workspace.getWorkspaceName())
                .append("<b> 에서 초대가 왔습니다.</p>")
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

    private MemberEntity getMemberOrThrowE001(Optional<MemberEntity> memberRepository) {
        return memberRepository.orElseThrow(
                () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
        );
    }

    private WorkspaceEntity getWorkspaceOrThrowE001(Optional<WorkspaceEntity> workspaceRepository) {
        return workspaceRepository.orElseThrow(
                () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
        );
    }

    private WorkspaceMemberEntity getWorkspaceMemberOrThrowA002(WorkspaceEntity inviteWorkspace, MemberEntity inviter) {
        return workspaceMemberRepository.findByWorkspaceAndMember(inviteWorkspace, inviter).orElseThrow(
                () -> new BusinessException(ErrorCode.AUTHORIZATION_FAILED)
        );
    }

}
