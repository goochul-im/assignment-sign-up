package com.thinkfree.tfinder.workspace.service.adapter;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IMailSendLimitRepository;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IEmailValidateRepository;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IPendingInviteRepository;
import com.thinkfree.tfinder.common.config.JwtProperties;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.common.exception.SignupRequireException;
import com.thinkfree.tfinder.common.util.jwt.dto.InviteTokenResult;
import com.thinkfree.tfinder.common.util.jwt.iface.IJwtManager;
import com.thinkfree.tfinder.workspace.domain.WorkspaceTier;
import com.thinkfree.tfinder.workspace.service.dto.*;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.common.infrastructure.external.iface.IMailSender;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.util.*;
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
    private IMailSendLimitRepository emailSendLimitRepository;
    @InjectMocks
    private WorkspaceService workspaceService;

    private static final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
            .build();

    @Test
    void 멤버가_속한_워크스페이스_목록을_조회할_수_있어야_한다() {


//        MemberEntity member1 = fixture.giveMeOne(MemberEntity.class);
//        System.out.println(member1);

        //given
        long memberId = 1L;
        MemberEntity member = getMember(memberId);
        WorkspaceEntity workspace = getWorkspace(1L);
        WorkspaceMemberEntity workspaceMember = getWorkspaceMember(workspace, member);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(workspaceMemberRepository.findAllByMember(member)).willReturn(List.of(workspaceMember));

        //when
        MyWorkspaceResponse myWorkspaces = workspaceService.getMyWorkspaces(memberId);
        List<WorkspaceResponse> result = myWorkspaces.workspaceList();

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
        MyWorkspaceResponse myWorkspaces = workspaceService.getMyWorkspaces(memberId);
        List<WorkspaceResponse> result = myWorkspaces.workspaceList();

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
        WorkspaceMemberEntity anotherWorkspaceMember = new WorkspaceMemberEntity(
                2L,
                workspace,
                member,
                WorkspaceMemberRole.MEMBER
        );

        given(memberRepository.findById(requesterId)).willReturn(Optional.of(requester));
        given(workspaceRepository.findById(workspaceId)).willReturn(Optional.of(workspace));
        given(workspaceMemberRepository.findByWorkspaceAndMember(workspace, requester))
                .willReturn(Optional.of(requesterWorkspaceMember));
        given(workspaceMemberRepository.findWorkspaceMemberPage(any(), any()))
                .willReturn(new PageImpl<>(
                        List.of(requesterWorkspaceMember, anotherWorkspaceMember)
                ));

        //when
        List<WorkspaceMemberResponse> result = workspaceService.getWorkspaceMembersPage(new getWorkspaceMembersPageCommand(requesterId, workspaceId, 0, 10)).memberList();

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
        BusinessException exception = assertThrows(BusinessException.class, () -> workspaceService.getWorkspaceMembersPage(
                new getWorkspaceMembersPageCommand(requesterId, workspaceId, 0, 10)));
        then(workspaceMemberRepository).should(never()).findWorkspaceMemberPage(any(), any());

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
        assertThrows(SignupRequireException.class, () -> workspaceService.acceptInvite(token));
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

        given(emailSendLimitRepository.getRemainLimit(anyInt(), anyLong())).willReturn(10);

        //when
        InviteResponse result = workspaceService.inviteMember(emailList, memberId, workspaceId);

        //then
        assertThat(result.alreadyJoinedEmails()).containsExactlyInAnyOrder(alreadyJoinedEmail);
        assertThat(result.inviteSuccessEmails()).containsExactlyInAnyOrder(successEmail);
    }

    @Test
    void 초대_인원_제한을_초과하여_초대를_발송하면_TOO_MANY_INVITE_예외를_던진다(){
        //given
        IntStream ints = new Random().ints(100,0,100);
        List<String> list = ints.boxed().map(String::valueOf).toList();
        long memberId = 1L;
        long workspaceId = 1L;
        MemberEntity member = getMember(memberId);
        WorkspaceEntity workspace = getWorkspace(workspaceId);
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(workspaceRepository.findById(any())).willReturn(Optional.of(workspace));
        WorkspaceMemberEntity workspaceMember = new WorkspaceMemberEntity(workspace, member, WorkspaceMemberRole.OWNER);
        given(workspaceMemberRepository.findByWorkspaceAndMember(workspace, member)).willReturn(Optional.of(workspaceMember));
        given(emailSendLimitRepository.getRemainLimit(anyInt(), anyLong())).willReturn(10);

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
        CreateWorkspaceCommand dto = new CreateWorkspaceCommand(requestMemberId, workspaceName, workspaceUrl);

        given(workspaceRepository.existsByWorkspaceUrl(any())).willReturn(false);
        given(memberRepository.findById(any())).willReturn(Optional.of(getMember(requestMemberId)));

        WorkspaceEntity workspace = new WorkspaceEntity(
                1L,
                workspaceName,
                workspaceUrl,
                0L,
                false,
                null,
                WorkspaceTier.FREE,
                Locale.KOREA
        );

        given(workspaceRepository.save(any())).willReturn(workspace);

        //when
        CreateWorkspaceResponse result = workspaceService.create(dto);

        //then
        assertThat(result.workspaceId()).isEqualTo(1L);
        assertThat(result.workspaceUrl()).isEqualTo(workspaceUrl);
        assertThat(result.workspaceName()).isEqualTo(workspaceName);
    }

    @Test
    void 워크스페이스를_생성할_때_워크스페이스_URL이_중복되면_DUPLICATE_ERROR_예외를_던진다(){
        //given
        long requestMemberId = 1L;
        String workspaceName = "test";
        String workspaceUrl = "testUrl";
        CreateWorkspaceCommand dto = new CreateWorkspaceCommand(requestMemberId, workspaceName, workspaceUrl);
        given(memberRepository.findById(any())).willReturn(Optional.of(getMember(requestMemberId)));
        given(workspaceRepository.existsByWorkspaceUrl(any())).willReturn(true);

        //when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> workspaceService.create(dto));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_ERROR);
    }

    @Test
    void 워크스페이스를_삭제할_때_권한이_없다면_예외를_던진다(){
        //given
        long requesterId = 1L;
        long workspaceId = 1L;
        MemberEntity member = getMember(requesterId);
        WorkspaceEntity workspace = getWorkspace(workspaceId);
        WorkspaceMemberEntity workspaceMember = getWorkspaceMember(workspace, member, WorkspaceMemberRole.MEMBER);

        given(memberRepository.findById(requesterId)).willReturn(Optional.of(member));
        given(workspaceRepository.findById(workspaceId)).willReturn(Optional.of(workspace));
        given(workspaceMemberRepository.findByWorkspaceAndMember(workspace, member))
                .willReturn(Optional.of(workspaceMember));

        //when & then
        assertThrows(BusinessException.class, () -> workspaceService.delete(requesterId, workspaceId));
    }


    private WorkspaceMemberEntity getWorkspaceMember(WorkspaceEntity workspace, MemberEntity member) {
        return new WorkspaceMemberEntity(
                1L,
                workspace,
                member,
                WorkspaceMemberRole.OWNER
        );
    }

    private WorkspaceMemberEntity getWorkspaceMember(WorkspaceEntity workspace, MemberEntity member, WorkspaceMemberRole role) {
        return new WorkspaceMemberEntity(
                1L,
                workspace,
                member,
                role
        );
    }

    private WorkspaceEntity getWorkspace(long id) {
        return new WorkspaceEntity(
                id,
                "testWorkspace",
                "testUrl",
                100L,
                false,
                null,
                WorkspaceTier.FREE,
                Locale.KOREA
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
