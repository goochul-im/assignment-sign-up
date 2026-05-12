package com.thinkfree.tfinder.workspace.service.adapter;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IEmailValidateRepository;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IPendingInviteRepository;
import com.thinkfree.tfinder.common.config.JwtProperties;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.common.infrastructure.persitence.dto.InviteMessageDto;
import com.thinkfree.tfinder.common.infrastructure.persitence.iface.IMessageQueue;
import com.thinkfree.tfinder.common.service.dto.InviteTokenResult;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import com.thinkfree.tfinder.workspace.domain.MessageKey;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import com.thinkfree.tfinder.workspace.service.dto.InviteResultDto;
import com.thinkfree.tfinder.workspace.service.dto.MyWorkspacesResultDto;
import com.thinkfree.tfinder.workspace.service.dto.CreateWorkspaceDto;
import com.thinkfree.tfinder.workspace.service.dto.WorkspaceMemberResultDto;
import com.thinkfree.tfinder.workspace.service.iface.IWorkspaceQuery;
import com.thinkfree.tfinder.workspace.service.iface.IWorkspaceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WorkspaceService implements IWorkspaceUseCase, IWorkspaceQuery {

    private final IWorkspaceMemberRepository workspaceMemberRepository;
    private final IMemberRepository memberRepository;
    private final IWorkspaceRepository workspaceRepository;
    private final IJwtManager jwtManager;
    private final IEmailValidateRepository emailValidateRepository;
    private final IPendingInviteRepository pendingInviteRepository;
    private final IMessageQueue messageQueue;
    private final JwtProperties jwtProperties;

    @Value("${frontend.url}")
    private String FRONTEND_URL;

    @Override
    @Transactional
    public WorkspaceEntity create(CreateWorkspaceDto dto) throws BusinessException {
        MemberEntity creator = getMemberOrThrow(dto.requestMemberId());

        if (workspaceRepository.existsByWorkspaceName(dto.workspaceName()) || workspaceRepository.existsByWorkspaceUrl(dto.workspaceUrl())) {
            throw new BusinessException(ErrorCode.DUPLICATE_ERROR);
        }

        WorkspaceEntity workspace = new WorkspaceEntity(
                dto.workspaceName(),
                dto.workspaceUrl()
        );
        workspace = workspaceRepository.save(workspace);

        WorkspaceMemberEntity workspaceMember = new WorkspaceMemberEntity(
                workspace,
                creator,
                WorkspaceMemberRole.OWNER
        );
        workspaceMemberRepository.save(workspaceMember);

        return workspace;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyWorkspacesResultDto> findMyWorkspaces(long memberId) throws BusinessException {
        MemberEntity member = getMemberOrThrow(memberId);

        return workspaceMemberRepository.findAllByMember(member)
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
    @Transactional(readOnly = true)
    public List<WorkspaceMemberResultDto> findWorkspaceMembers(long requesterId, long workspaceId) throws BusinessException {
        MemberEntity requester = getMemberOrThrow(requesterId);
        WorkspaceEntity workspace = getWorkspaceOrThrow(workspaceId);

        getWorkspaceMemberOrThrow(workspace, requester); // requester와 workspace의 연관관계가 없으면 바로 예외 던져짐

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
    @Transactional(readOnly = true)
    public InviteResultDto inviteMember(List<String> toEmailList, long inviterId, long workspaceId) throws BusinessException{

        if (toEmailList.size() > 50) {
            throw new BusinessException(ErrorCode.TOO_MANY_INVITE);
        }

        MemberEntity inviter = getMemberOrThrow(inviterId);
        WorkspaceEntity inviteWorkspace = getWorkspaceOrThrow(workspaceId);
        WorkspaceMemberEntity workspaceMember = getWorkspaceMemberOrThrow(inviteWorkspace, inviter);

        WorkspaceMemberRole role = workspaceMember.getRole();
        if (!(role == WorkspaceMemberRole.MANAGER || role == WorkspaceMemberRole.OWNER)) {
            throw new BusinessException(ErrorCode.AUTHORIZATION_FAILED);
        }

        Set<String> joinedEmails = memberRepository.findJoinedEmails(inviteWorkspace, toEmailList);
        Set<String> emailsSet = new HashSet<>(toEmailList);
        emailsSet.removeAll(joinedEmails); // 이미 가입한 이메일을 제거

        ArrayList<String> failed = new ArrayList<>();
        ArrayList<String> success = new ArrayList<>();
        for (String toEmail : emailsSet) {
            // 이미 워크스페이스에 가입한 경우에는 이메일을 또 보낼 필요 없다

            String inviteToken = jwtManager.generateInviteToken(
                    inviter.getEmail(),
                    toEmail,
                    inviteWorkspace.getWorkspaceUrl(),
                    Instant.now().plusSeconds(jwtProperties.getInviteExpirationSeconds())
            );

            String subject = "tfinder 워크스페이스 초대";

            boolean publish = messageQueue.publish(MessageKey.INVITE, new InviteMessageDto(
                    Instant.now().toString(),
                    toEmail,
                    subject
                    , makeInviteMailMessage(inviteWorkspace, inviteToken)
            ));

            if (publish) {
                success.add(toEmail);
            } else {
                failed.add(toEmail);
            }

        }

        return new InviteResultDto(
                success,
                failed,
                new ArrayList<>(joinedEmails)
        );
    }

    @Override
    @Transactional
    public void acceptInvite(String token) throws BusinessException{

        InviteTokenResult result = jwtManager.parsingInviteToken(token);
        String toEmail = result.toEmail();
        String workspaceUrl = result.workspaceUrl();
        if (memberRepository.existsByEmail(toEmail)) {
            // 이미 회원일 경우
            MemberEntity member = getMemberOrThrow(toEmail);
            WorkspaceEntity workspace = getWorkspaceOrThrow(workspaceUrl);

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
            emailValidateRepository.saveAsValidated(toEmail, expiration);
            pendingInviteRepository.save(toEmail, workspaceUrl, expiration);
            throw new BusinessException(ErrorCode.SIGNUP_FIRST);
        }

    }

    private String makeInviteMailMessage(WorkspaceEntity workspace, String token) {
        // 들어가야 할 정보
        // 클릭할 URL + 메시지 내용
        String inviteUrl = FRONTEND_URL + "?token=" + token;

        String message = """
                <h2>tfinder 워크스페이스 초대</h2>
                <p><b>%s</b>에서 초대가 왔습니다</p>
                <p>아래 링크를 눌러 참가하세요.</p>
                <p>만약 서비스에 아직 가입하지 않았다면 아래 초대 링크를 누른 후 10분 이내에 가입을 마쳐주세요.</p>
                <p><a href="%s">참가하기</a></p>
                <p>링크가 열리지 않는다면 아래 주소를 복사해서 브라우저에 붙여넣어 주세요.</p>
                <p>%s</p>
                """.formatted(workspace.getWorkspaceName(), inviteUrl, inviteUrl);

        return message;
    }

    private MemberEntity getMemberOrThrow(long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
        );
    }

    private MemberEntity getMemberOrThrow(String email) {
        return memberRepository.findByEmail(email).orElseThrow(
                () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
        );
    }

    private WorkspaceEntity getWorkspaceOrThrow(long workspaceId) {
        return workspaceRepository.findById(workspaceId).orElseThrow(
                () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
        );
    }

    private WorkspaceEntity getWorkspaceOrThrow(String workspaceUrl) {
        return workspaceRepository.findByWorkspaceUrl(workspaceUrl).orElseThrow(
                () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
        );
    }

    private WorkspaceMemberEntity getWorkspaceMemberOrThrow(WorkspaceEntity workspace, MemberEntity member) {
        return workspaceMemberRepository.findByWorkspaceAndMember(workspace, member).orElseThrow(
                () -> new BusinessException(ErrorCode.AUTHORIZATION_FAILED)
        );
    }

}
