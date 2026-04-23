package com.thinkfree.tfinder.workspace.service.adapter;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.service.dto.InviteTokenResult;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.domain.MemberType;
import com.thinkfree.tfinder.workspace.infrastructure.external.iface.IMailSender;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter.MemberJpaRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter.WorkspaceJpaRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.adapter.WorkspaceMemberJpaRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class WorkspaceServiceTest {

    @Mock
    private WorkspaceMemberJpaRepository workspaceMemberRepository;
    @Mock
    private MemberJpaRepository memberRepository;
    @Mock
    private WorkspaceJpaRepository workspaceRepository;
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

        MemberEntity member = new MemberEntity(
                1L,
                "testUser",
                toEmail,
                "testPasswd",
                MemberType.DEFAULT
        );

        WorkspaceEntity workspace = new WorkspaceEntity(
                1L,
                "testWorkspace",
                "testUrl",
                100L,
                false
        );

        WorkspaceMemberEntity workspaceMember = new WorkspaceMemberEntity(
                1L,
                WorkspaceMemberRole.MEMBER,
                false,
                Instant.now(),
                workspace.getId(),
                member.getId()
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
