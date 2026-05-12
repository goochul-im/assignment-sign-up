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
import com.thinkfree.tfinder.common.infrastructure.external.iface.IMailSender;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import com.thinkfree.tfinder.workspace.service.dto.CreateWorkspaceDto;
import com.thinkfree.tfinder.workspace.service.dto.InviteResultDto;
import com.thinkfree.tfinder.workspace.service.dto.MyWorkspacesResultDto;
import com.thinkfree.tfinder.workspace.service.dto.WorkspaceMemberResultDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class WorkspaceServiceTest {

    @Mock
    private IWorkspaceMemberRepository workspaceMemberRepository;
    @Mock
    private IMemberRepository memberRepository;
    @Mock
    private IWorkspaceRepository workspaceRepository;
    @Mock
    private IMailSender mailSender;
    @Mock
    private IJwtManager jwtManager;
    @Mock
    JwtProperties jwtProperties;
    @Mock
    IEmailValidateRepository emailValidateRepository;
    @Mock
    IPendingInviteRepository pendingInviteRepository;
    @Mock
    IMessageQueue messageQueue;
    @InjectMocks
    private WorkspaceService workspaceService;

    @Test
    void 멤버가_속한_워크스페이스_목록을_조회할_수_있어야_한다() {
        //given
        long memberId = 1L;
        MemberEntity member = getMember(memberId);
        WorkspaceEntity workspace = getWorkspace(1L);
        WorkspaceMemberEntity workspaceMember = getWorkspaceMember(workspace, member);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(workspaceMemberRepository.findAllByMember(member)).willReturn(List.of(workspaceMember));

        //when
        List<MyWorkspacesResultDto> result = workspaceService.findMyWorkspaces(memberId);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().workspaceId()).isEqualTo(workspace.getId());
        assertThat(result.getFirst().workspaceName()).isEqualTo(workspace.getWorkspaceName());
        assertThat(result.getFirst().workspaceUrl()).isEqualTo(workspace.getWorkspaceUrl());
        assertThat(result.getFirst().role()).isEqualTo(WorkspaceMemberRole.OWNER);
    }

    @Test
    void 아직_아무_워크스페이스에_가입하지_않았을_경우_빈_리스트를_반환해야_한다() {
        //given
        long memberId = 1L;
        MemberEntity member = getMember(memberId);
        WorkspaceEntity workspace = getWorkspace(1L);
        WorkspaceMemberEntity workspaceMember = getWorkspaceMember(workspace, member);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(workspaceMemberRepository.findAllByMember(member)).willReturn(List.of());

        //when
        List<MyWorkspacesResultDto> result = workspaceService.findMyWorkspaces(memberId);

        //then
        assertThat(result).hasSize(0);
    }


    @Test
    void 워크스페이스에_속한_멤버_목록을_조회할_수_있어야_한다() {
        //given
        long requesterId = 1L;
        long workspaceId = 10L;
        MemberEntity requester = getMember(1L);
        MemberEntity member = getMember(2L);
        WorkspaceEntity workspace = getWorkspace(workspaceId);
        WorkspaceMemberEntity requesterWorkspaceMember = getWorkspaceMember(workspace, requester);
        WorkspaceMemberEntity memberWorkspaceMember = new WorkspaceMemberEntity(
                workspace,
                member,
                WorkspaceMemberRole.MEMBER
        );

        given(memberRepository.findById(requesterId)).willReturn(Optional.of(requester));
        given(workspaceRepository.findById(workspaceId)).willReturn(Optional.of(workspace));
        given(workspaceMemberRepository.findByWorkspaceAndMember(workspace, requester))
                .willReturn(Optional.of(requesterWorkspaceMember));
        given(workspaceMemberRepository.findAllMemberByWorkspace(workspace))
                .willReturn(List.of(requesterWorkspaceMember, memberWorkspaceMember));

        //when
        List<WorkspaceMemberResultDto> result = workspaceService.findWorkspaceMembers(requesterId, workspaceId);

        //then
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().memberId()).isEqualTo(requester.getId());
        assertThat(result.getFirst().nickname()).isEqualTo(requester.getNickname());
        assertThat(result.getFirst().email()).isEqualTo(requester.getEmail());
        assertThat(result.getFirst().role()).isEqualTo(WorkspaceMemberRole.OWNER);
    }

    @Test
    void 워크스페이스에_속하지_않은_멤버가_멤버_목록을_조회하면_AUTHORIZATION_FAILED_예외를_던진다() {
        //given
        long requesterId = 1L;
        long workspaceId = 10L;
        MemberEntity requester = getMember(requesterId);
        WorkspaceEntity workspace = getWorkspace(workspaceId);

        given(memberRepository.findById(requesterId)).willReturn(Optional.of(requester));
        given(workspaceRepository.findById(workspaceId)).willReturn(Optional.of(workspace));
        given(workspaceMemberRepository.findByWorkspaceAndMember(workspace, requester))
                .willReturn(Optional.empty());

        //when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> workspaceService.findWorkspaceMembers(requesterId, workspaceId));
        then(workspaceMemberRepository).should(never()).findAllMemberByWorkspace(any());

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTHORIZATION_FAILED);
    }

    @Test
    void 서비스에_이미_가입한_멤버가_초대를_수락하면_워크스페이스_멤버가_된다(){
        //given
        String token = "thisistesttoken";
        String toEmail = "to@email.com";

        InviteTokenResult tokenResult = new InviteTokenResult(
                toEmail,
                "from@email.com",
                "testworkspace"
        );

        MemberEntity member = getMember(1L);

        WorkspaceEntity workspace = getWorkspace(1L);


        given(jwtManager.parsingInviteToken(token)).willReturn(tokenResult);
        given(memberRepository.existsByEmail(toEmail)).willReturn(true);
        given(memberRepository.findByEmail(toEmail)).willReturn(java.util.Optional.of(member));
        given(workspaceRepository.findByWorkspaceUrl(any())).willReturn(java.util.Optional.of(workspace));

        //when
        workspaceService.acceptInvite(token);

        //then
        then(workspaceMemberRepository).should(times(1)).save(any());
    }

    @Test
    void 아직_가입하지_않고_초대를_수락하면_예외를_던진다(){
        //given
        String token = "thisistesttoken";
        String toEmail = "to@email.com";
        long validateEmailExpirationSecond = 3000L;

        InviteTokenResult tokenResult = new InviteTokenResult(
                toEmail,
                "from@email.com",
                "testworkspace"
        );

        given(jwtManager.parsingInviteToken(token)).willReturn(tokenResult);
        given(memberRepository.existsByEmail(toEmail)).willReturn(false);
        given(jwtProperties.getValidateEmailExpirationSeconds()).willReturn(validateEmailExpirationSecond);

        //when & then
        BusinessException businessException = assertThrows(BusinessException.class, () -> workspaceService.acceptInvite(token));

        //then
        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.SIGNUP_FIRST);
    }

    @Test
    void 이미_가입한_워크스페이스_초대를_다시_수락하면_DUPLICATE_ERROR_예외를_던진다(){
        //given
        String token = "thisistesttoken";
        String toEmail = "to@email.com";

        InviteTokenResult tokenResult = new InviteTokenResult(
                toEmail,
                "from@email.com",
                "testworkspace"
        );

        given(jwtManager.parsingInviteToken(token)).willReturn(tokenResult);
        given(memberRepository.existsByEmail(any())).willReturn(true);
        given(workspaceMemberRepository.existsByWorkspaceAndMember(any(), any())).willReturn(true);
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(getMember(1L)));
        given(workspaceRepository.findByWorkspaceUrl(any())).willReturn(Optional.of(getWorkspace(1L)));

        //when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> workspaceService.acceptInvite(token));

        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_ERROR);
    }

    @Test
    void 초대를_보내고_해당_결과를_받을_수_있다(){
        //given
        String alreadyJoinedEmail = "alredy@email.com";
        String successEmail = "success@email.com";

        List<String> emailList = List.of(alreadyJoinedEmail, successEmail);

        long memberId = 1L;
        long workspaceId = 1L;
        MemberEntity member = getMember(memberId);
        WorkspaceEntity workspace = getWorkspace(workspaceId);
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(workspaceRepository.findById(any())).willReturn(Optional.of(workspace));
        WorkspaceMemberEntity workspaceMember = new WorkspaceMemberEntity(workspace, member, WorkspaceMemberRole.OWNER);
        given(workspaceMemberRepository.findByWorkspaceAndMember(workspace, member)).willReturn(Optional.of(workspaceMember));

        given(memberRepository.findJoinedEmails(workspace, emailList)).willReturn(Set.of(alreadyJoinedEmail));
        given(messageQueue.publish(any(), any())).willReturn(true);

        //when
        InviteResultDto result = workspaceService.inviteMember(emailList, memberId, workspaceId);

        //then
        assertThat(result.alreadyJoinedEmails()).containsExactlyInAnyOrder(alreadyJoinedEmail);
        assertThat(result.inviteSuccessEmails()).containsExactlyInAnyOrder(successEmail);
    }

    @Test
    void 초대_인원_제한을_초과하여_초대를_발송하면_TOO_MANY_INVITE_예외를_던진다(){
        //given
        IntStream ints = new Random().ints(100,0,100);
        List<String> list = ints.boxed().map(String::valueOf).toList();

        //when & then
        BusinessException businessException = assertThrows(BusinessException.class, () -> workspaceService.inviteMember(list, 1L, 1L));

        //then
        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.TOO_MANY_INVITE);
    }

    @Test
    void 권한이_없는_멤버가_초대를_발송하면_AUTHORIZATION_FAILED_예외를_던진다(){
        //given
        IntStream ints = new Random().ints(50,0,100);
        List<String> list = ints.boxed().map(String::valueOf).toList();

        long memberId = 1L;
        long workspaceId = 1L;
        MemberEntity member = getMember(memberId);
        WorkspaceEntity workspace = getWorkspace(workspaceId);
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(workspaceRepository.findById(any())).willReturn(Optional.of(workspace));
        WorkspaceMemberEntity workspaceMember = new WorkspaceMemberEntity(workspace, member, WorkspaceMemberRole.MEMBER);
        given(workspaceMemberRepository.findByWorkspaceAndMember(workspace, member)).willReturn(Optional.of(workspaceMember));

        //when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> workspaceService.inviteMember(list, memberId, workspaceId));

        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTHORIZATION_FAILED);
    }

    @Test
    void 워크스페이스를_생성할수_있어야_한다(){
        //given
        long requestMemberId = 1L;
        String workspaceName = "test";
        String workspaceUrl = "testUrl";
        CreateWorkspaceDto dto = new CreateWorkspaceDto(requestMemberId, workspaceName, workspaceUrl);

        given(workspaceRepository.existsByWorkspaceName(any())).willReturn(false);
        given(workspaceRepository.existsByWorkspaceUrl(any())).willReturn(false);
        given(memberRepository.findById(any())).willReturn(Optional.of(getMember(requestMemberId)));

        WorkspaceEntity workspace = new WorkspaceEntity(
                1L,
                workspaceName,
                workspaceUrl,
                0L,
                false
        );
        given(workspaceRepository.save(any())).willReturn(workspace);

        //when
        WorkspaceEntity result = workspaceService.create(dto);

        //then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getWorkspaceName()).isEqualTo(workspaceName);
        assertThat(result.getWorkspaceUrl()).isEqualTo(workspaceUrl);
    }

    @Test
    void 워크스페이스를_생성할_때_워크스페이스_이름이_중복되면_DUPLICATE_ERROR_예외를_던진다(){
        //given
        long requestMemberId = 1L;
        String workspaceName = "test";
        String workspaceUrl = "testUrl";
        CreateWorkspaceDto dto = new CreateWorkspaceDto(requestMemberId, workspaceName, workspaceUrl);
        given(memberRepository.findById(any())).willReturn(Optional.of(getMember(requestMemberId)));
        given(workspaceRepository.existsByWorkspaceName(any())).willReturn(true);

        //when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> workspaceService.create(dto));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_ERROR);
    }

    @Test
    void 워크스페이스를_생성할_때_워크스페이스_URL이_중복되면_DUPLICATE_ERROR_예외를_던진다(){
        //given
        long requestMemberId = 1L;
        String workspaceName = "test";
        String workspaceUrl = "testUrl";
        CreateWorkspaceDto dto = new CreateWorkspaceDto(requestMemberId, workspaceName, workspaceUrl);
        given(memberRepository.findById(any())).willReturn(Optional.of(getMember(requestMemberId)));
        given(workspaceRepository.existsByWorkspaceName(any())).willReturn(false);
        given(workspaceRepository.existsByWorkspaceUrl(any())).willReturn(true);

        //when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> workspaceService.create(dto));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_ERROR);
    }


    private WorkspaceMemberEntity getWorkspaceMember(WorkspaceEntity workspace, MemberEntity member) {
        return new WorkspaceMemberEntity(
                workspace,
                member,
                WorkspaceMemberRole.OWNER
        );
    }

    private WorkspaceEntity getWorkspace(long id) {
        return new WorkspaceEntity(
                id,
                "testWorkspace",
                "testUrl",
                100L,
                false
        );
    }

    private MemberEntity getMember(long memberId) {
        return new MemberEntity(
                memberId,
                "testUser",
                "test@email.com",
                "testPasswd"
        );
    }


}
