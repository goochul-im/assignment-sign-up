package com.thinkfree.tfinder.workspace.infrastructure.persistence;

import com.thinkfree.tfinder.annotation.IntegrationTest;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@IntegrationTest
@ActiveProfiles("test")
class IWorkspaceMemberRepositoryTest {

    @Autowired
    private IWorkspaceMemberRepository workspaceMemberRepository;
    @Autowired
    private IMemberRepository memberRepository;
    @Autowired
    private IWorkspaceRepository workspaceRepository;

    @Test
    void 워크스페이스와_이메일로_워크스페이스_멤버_존재_여부를_확인할_수_있다(){
        //given
        String testemail = "testemail";
        MemberEntity member = new MemberEntity(
                "test",
                testemail,
                "test"
        );
        WorkspaceEntity workspace = new WorkspaceEntity(
                "testWorkspace",
                "testUrl"
        );
        WorkspaceMemberEntity workspaceMember = new WorkspaceMemberEntity(
                workspace,
                member,
                WorkspaceMemberRole.MEMBER
        );
        member = memberRepository.save(member);
        workspace = workspaceRepository.save(workspace);
        workspaceMember = workspaceMemberRepository.save(workspaceMember);

        //when
        boolean result = workspaceMemberRepository.existsByWorkspaceAndMemberEmail(workspace, testemail);
        boolean noResult = workspaceMemberRepository.existsByWorkspaceAndMemberEmail(workspace, "noEmail");

        //then
        assertThat(result).isTrue();
        assertThat(noResult).isFalse();
    }

    @Test
    void 멤버가_속한_워크스페이스를_모두_같이_가져올_수_있다(){
        //given
        String testemail = "testemail";
        MemberEntity member = new MemberEntity(
                "test",
                testemail,
                "test"
        );
        member = memberRepository.save(member);

        int repeat = 4;
        for (int i = 0; i < repeat; i++) {
            WorkspaceEntity workspace = new WorkspaceEntity(
                    "testWorkspace" + i,
                    "testUrl" + i
            );
            workspaceRepository.save(workspace);
            WorkspaceMemberEntity workspaceMember = new WorkspaceMemberEntity(
                    workspace,
                    member,
                    WorkspaceMemberRole.MEMBER
            );
            workspaceMemberRepository.save(workspaceMember);
        }

        //when
        List<WorkspaceMemberEntity> result = workspaceMemberRepository.findAllByMember(member);
        MemberEntity member1 = result.getFirst().getMember();
        MemberEntity member2 = result.getLast().getMember();

        //then
        assertThat(result).hasSize(repeat);
        assertThat(member1).usingRecursiveComparison().isEqualTo(member2);
        // 리스트 객체 비교??
    }

    @Test
    void 멤버가_속한_워크스페이스가_없으면_빈_리스트를_반환한다(){
        //given
        String testemail = "testemail";
        MemberEntity member = new MemberEntity(
                "test",
                testemail,
                "test"
        );
        member = memberRepository.save(member);

        //when
        List<WorkspaceMemberEntity> result = workspaceMemberRepository.findAllByMember(member);

        //then
        assertThat(result).hasSize(0);
    }

    @Test
    void 워크스페이스에_속한_멤버들을_모두_가져올_수_있다(){
        //given
        int repeat = 4;
        WorkspaceEntity workspace = new WorkspaceEntity(
                "testName",
                "testUrl"
        );
        workspace = workspaceRepository.save(workspace);
        for (int i = 0; i < repeat; i++) {
            MemberEntity member = getMember("test" + i);

            member = memberRepository.save(member);
            WorkspaceMemberEntity workspaceMember = new WorkspaceMemberEntity(workspace, member, WorkspaceMemberRole.MEMBER);
            workspaceMemberRepository.save(workspaceMember);
        }

        //when
        List<WorkspaceMemberEntity> result = workspaceMemberRepository.findAllMemberByWorkspace(workspace);
        WorkspaceEntity workspace1 = result.getFirst().getWorkspace();
        WorkspaceEntity workspace2 = result.getFirst().getWorkspace();

        //then
        assertThat(result).hasSize(repeat);
        assertThat(workspace1).usingRecursiveComparison().isEqualTo(workspace2);
    }

    private MemberEntity getMember(String email) {
        return new MemberEntity(
                "testNickname",
                email,
                "testPassword"
        );
    }


}
