package com.thinkfree.tfinder.workspace.service.adapter;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IEmailValidateRepository;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IPendingInviteRepository;
import com.thinkfree.tfinder.common.config.JwtProperties;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.common.infrastructure.messagequeue.dto.InviteMessageDto;
import com.thinkfree.tfinder.common.infrastructure.messagequeue.iface.IMessageQueue;
import com.thinkfree.tfinder.common.service.dto.InviteTokenResult;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import com.thinkfree.tfinder.workspace.service.dto.CreateWorkspaceResponse;
import com.thinkfree.tfinder.workspace.service.dto.InviteResponse;
import com.thinkfree.tfinder.workspace.service.dto.MyWorkspaceResponse;
import com.thinkfree.tfinder.workspace.service.dto.WorkspaceMembersPageResponse;
import com.thinkfree.tfinder.workspace.domain.MessageKey;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import com.thinkfree.tfinder.workspace.service.dto.*;
import com.thinkfree.tfinder.workspace.service.iface.IWorkspaceQuery;
import com.thinkfree.tfinder.workspace.service.iface.IWorkspaceUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
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
    public CreateWorkspaceResponse create(CreateWorkspaceCommand dto) throws BusinessException {
        MemberEntity creator = getMemberOrThrow(dto.requestMemberId());

        if (workspaceRepository.existsByWorkspaceUrl(dto.workspaceUrl())) {
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

        return new CreateWorkspaceResponse(
                workspace.getId(),
                workspace.getWorkspaceUrl(),
                workspace.getWorkspaceName());
    }

    @Override
    @Transactional(readOnly = true)
    public MyWorkspaceResponse getMyWorkspaces(long memberId) throws BusinessException {
        MemberEntity member = getMemberOrThrow(memberId);
        List<WorkspaceMemberEntity> list = workspaceMemberRepository.findAllByMember(member);

        return new MyWorkspaceResponse(
                list
        );
    }

    @Override
    @Transactional(readOnly = true)
    public WorkspaceMembersPageResponse getWorkspaceMembersPage(long requesterId, long workspaceId, int page, int pageSize) throws BusinessException {
        MemberEntity requester = getMemberOrThrow(requesterId);
        WorkspaceEntity workspace = getWorkspaceOrThrow(workspaceId);

        getWorkspaceMemberOrThrow(workspace, requester); // requester와 workspace의 연관관계가 없으면 바로 예외 던져짐

        Page<WorkspaceMemberEntity> workspaceMemberPage = workspaceMemberRepository.findWorkspaceMemberPage(workspace, PageRequest.of(page, pageSize));
        return new WorkspaceMembersPageResponse(workspaceMemberPage);
    }

    @Override
    @Transactional(readOnly = true)
    public InviteResponse inviteMember(List<String> toEmailList, long inviterId, long workspaceId) throws BusinessException {

        if (toEmailList.size() > 50) {
            throw new BusinessException(ErrorCode.TOO_MANY_INVITE);
        }

        MemberEntity inviter = getMemberOrThrow(inviterId);
        WorkspaceEntity inviteWorkspace = getWorkspaceOrThrow(workspaceId);
        WorkspaceMemberEntity workspaceMember = getWorkspaceMemberOrThrow(inviteWorkspace, inviter);

        WorkspaceMemberRole role = workspaceMember.getRole();
        if (!(WorkspaceMemberRole.MANAGER.equals(role) || WorkspaceMemberRole.OWNER.equals(role))) {
            throw new BusinessException(ErrorCode.AUTHORIZATION_FAILED);
        }

        Set<String> joinedEmails = memberRepository.findJoinedEmails(inviteWorkspace, toEmailList);
        Set<String> emailsSet = new HashSet<>(toEmailList);
        emailsSet.removeAll(joinedEmails); // 이미 가입한 이메일을 제거

        ArrayList<String> failed = new ArrayList<>();
        ArrayList<String> success = new ArrayList<>();
        ArrayList<String> alreadyJoined = new ArrayList<>(joinedEmails);

        for (String toEmail : emailsSet) {
            // 이미 워크스페이스에 가입한 경우에는 이메일을 또 보낼 필요 없다

            String inviteToken = jwtManager.generateInviteToken(
                    inviter.getEmail(),
                    toEmail,
                    inviteWorkspace.getWorkspaceUrl()
            );
            log.info("invite Token = {}", inviteToken);

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

        return new InviteResponse(
                success,
                failed,
                alreadyJoined
        );
    }

    @Override
    @Transactional
    public void acceptInvite(String token) throws BusinessException {

        InviteTokenResult result = jwtManager.parsingInviteToken(token);
        String toEmail = result.toEmail();
        String workspaceUrl = result.workspaceUrl();
        if (memberRepository.existsByEmail(toEmail)) {
            // 이미 회원일 경우
            MemberEntity member = getMemberOrThrow(toEmail);
            WorkspaceEntity workspace = getWorkspaceOrThrow(workspaceUrl);

            if (workspaceMemberRepository.existsByWorkspaceAndMember(workspace, member)) {
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
            emailValidateRepository.saveAsValidated(toEmail, expiration);    // emailValidate와 pendingInvite는 하나의 트랜잭션으로 묶여서
            pendingInviteRepository.save(toEmail, workspaceUrl, expiration); // 쓰기 지연을 통해 에러가 발생하면 모두 저장되지 않음
            throw new BusinessException(ErrorCode.SIGNUP_FIRST);
        }

    }

    @Override
    @Transactional
    public void delete(long workspaceId, long requesterId) {
        WorkspaceEntity workspace = getWorkspaceOrThrow(workspaceId);
        MemberEntity member = getMemberOrThrow(requesterId);
        WorkspaceMemberEntity workspaceMember = getWorkspaceMemberOrThrow(workspace, member);

        if (!WorkspaceMemberRole.OWNER.equals(workspaceMember.getRole())) {
            throw new BusinessException(ErrorCode.AUTHORIZATION_FAILED);
        }

        // 워크스페이스 소프트 딜리트
        workspace.delete();
        // 워크스페이스 멤버 삭제 처리
        workspaceMemberRepository.deleteAllByWorkspace(workspace);
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
