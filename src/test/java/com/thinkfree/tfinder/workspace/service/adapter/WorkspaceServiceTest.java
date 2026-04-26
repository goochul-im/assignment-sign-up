package com.thinkfree.tfinder.workspace.service.adapter;

import com.thinkfree.tfinder.common.exception.BusinessException;
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
import com.thinkfree.tfinder.workspace.service.dto.MyWorkspacesResultDto;
import com.thinkfree.tfinder.workspace.service.dto.WorkspaceMemberResultDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
    @InjectMocks
    private WorkspaceService workspaceService;

    @Test
    void 멤버가_속한_워크스페이스_목록을_조회할_수_있어야_한다() {
        //given
        long memberId = 1L;
        MemberEntity member = new MemberEntity(
                memberId,
                "testUser",
                "test@email.com",
                "testPasswd"
        );
        WorkspaceEntity workspace = new WorkspaceEntity(
                1L,
                "testWorkspace",
                "testUrl",
                100L,
                false
        );
        WorkspaceMemberEntity workspaceMember = new WorkspaceMemberEntity(
                workspace,
                member,
                WorkspaceMemberRole.OWNER,
                Instant.now()
        );

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(workspaceMemberRepository.findAllWorkspaceByMember(member)).willReturn(List.of(workspaceMember));

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
    void 워크스페이스에_속한_멤버_목록을_조회할_수_있어야_한다() {
        //given
        long requesterId = 1L;
        long workspaceId = 10L;
        MemberEntity requester = new MemberEntity(
                requesterId,
                "requester",
                "requester@email.com",
                "testPasswd"
        );
        MemberEntity member = new MemberEntity(
                2L,
                "member",
                "member@email.com",
                "testPasswd"
        );
        WorkspaceEntity workspace = new WorkspaceEntity(
                workspaceId,
                "testWorkspace",
                "testUrl",
                100L,
                false
        );
        WorkspaceMemberEntity requesterWorkspaceMember = new WorkspaceMemberEntity(
                workspace,
                requester,
                WorkspaceMemberRole.OWNER,
                Instant.now()
        );
        WorkspaceMemberEntity memberWorkspaceMember = new WorkspaceMemberEntity(
                workspace,
                member,
                WorkspaceMemberRole.MEMBER,
                Instant.now()
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
    void 워크스페이스에_속하지_않은_멤버가_멤버_목록을_조회하면_예외를_던진다() {
        //given
        long requesterId = 1L;
        long workspaceId = 10L;
        MemberEntity requester = new MemberEntity(
                requesterId,
                "requester",
                "requester@email.com",
                "testPasswd"
        );
        WorkspaceEntity workspace = new WorkspaceEntity(
                workspaceId,
                "testWorkspace",
                "testUrl",
                100L,
                false
        );

        given(memberRepository.findById(requesterId)).willReturn(Optional.of(requester));
        given(workspaceRepository.findById(workspaceId)).willReturn(Optional.of(workspace));
        given(workspaceMemberRepository.findByWorkspaceAndMember(workspace, requester))
                .willReturn(Optional.empty());

        //when & then
        assertThrows(BusinessException.class, () -> workspaceService.findWorkspaceMembers(requesterId, workspaceId));
        then(workspaceMemberRepository).should(never()).findAllMemberByWorkspace(any());
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

        MemberEntity member = new MemberEntity(
                1L,
                "testUser",
                toEmail,
                "testPasswd"
        );

        WorkspaceEntity workspace = new WorkspaceEntity(
                1L,
                "testWorkspace",
                "testUrl",
                100L,
                false
        );

        WorkspaceMemberEntity workspaceMember = new WorkspaceMemberEntity(
                workspace,
                member,
                WorkspaceMemberRole.MEMBER,
                Instant.now()
        );


        given(jwtManager.parsingInviteToken(token)).willReturn(tokenResult);
        given(memberRepository.existsByEmail(toEmail)).willReturn(true);
        given(memberRepository.findByEmail(toEmail)).willReturn(java.util.Optional.of(member));
        given(workspaceRepository.findByWorkspaceUrl(any())).willReturn(java.util.Optional.of(workspace));

        //when
        workspaceService.acceptMember(token);

        //then
        then(jwtManager).should(times(1)).parsingInviteToken(token);
        then(memberRepository).should(times(1)).existsByEmail(toEmail);
        then(memberRepository).should(times(1)).findByEmail(toEmail);
        then(workspaceRepository).should(times(1)).findByWorkspaceUrl(any());
        then(workspaceMemberRepository).should(times(1)).save(any());
    }

    @Test
    void 아직_가입하지_않고_초대를_수락하면_예외를_던진다(){
        //given
        String token = "thisistesttoken";
        String toEmail = "to@email.com";

        InviteTokenResult tokenResult = new InviteTokenResult(
                toEmail,
                "from@email.com",
                "testworkspace"
        );

        given(jwtManager.parsingInviteToken(token)).willReturn(tokenResult);
        given(memberRepository.existsByEmail(toEmail)).willReturn(false);

        //when & then
        assertThrows(BusinessException.class, () -> workspaceService.acceptMember(token));

    }

}
