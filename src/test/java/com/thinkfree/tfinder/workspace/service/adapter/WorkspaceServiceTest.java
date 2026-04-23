package com.thinkfree.tfinder.workspace.service.adapter;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.service.dto.InviteTokenResult;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import com.thinkfree.tfinder.workspace.domain.*;
import com.thinkfree.tfinder.workspace.infrastructure.external.iface.IMailSender;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.iface.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.iface.IWorkspaceMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.iface.IWorkspaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

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
    void 서비스에_이미_가입한_멤버가_초대를_수락하면_워크스페이스_멤버가_된다(){
        //given
        String token = "thisistesttoken";
        String toEmail = "to@email.com";

        InviteTokenResult tokenResult = new InviteTokenResult(
                toEmail,
                "from@email.com",
                "testworkspace"
        );

        Member member = new Member(
                1L,
                "testUser",
                toEmail,
                "testPasswd",
                MemberType.DEFAULT
        );

        Workspace workspace = new Workspace(
                1L,
                "testWorkspace",
                "testUrl",
                100L,
                false
        );

        WorkspaceMember workspaceMember = new WorkspaceMember(
                1L,
                member.getId(),
                workspace.getId(),
                WorkspaceMemberRole.MEMBER,
                false,
                Instant.now()
        );


        given(jwtManager.parsingInviteToken(token)).willReturn(tokenResult);
        given(memberRepository.isExistByEmail(toEmail)).willReturn(true);
        given(memberRepository.findByEmail(toEmail)).willReturn(member);
        given(workspaceRepository.findByUrl(any())).willReturn(workspace);

        //when
        workspaceService.acceptMember(token);

        //then
        then(jwtManager).should(times(1)).parsingInviteToken(token);
        then(memberRepository).should(times(1)).isExistByEmail(toEmail);
        then(memberRepository).should(times(1)).findByEmail(toEmail);
        then(workspaceRepository).should(times(1)).findByUrl(any());
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
        given(memberRepository.isExistByEmail(toEmail)).willReturn(false);

        //when & then
        assertThrows(BusinessException.class, () -> workspaceService.acceptMember(token));

        //then
    }

}
