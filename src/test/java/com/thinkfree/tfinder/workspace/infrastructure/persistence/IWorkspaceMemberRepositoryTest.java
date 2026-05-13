package com.thinkfree.tfinder.workspace.infrastructure.persistence;

import com.thinkfree.tfinder.annotation.IntegrationTest;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    void 워크스페이스에_속한_멤버들을_페이지로_가져올_수_있다(){
        //given
        int repeat = 35;
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
        int pageSize = 10;
        Page<WorkspaceMemberEntity> pages0 = workspaceMemberRepository.findWorkspaceMemberPage(workspace, PageRequest.of(0, pageSize));
        Page<WorkspaceMemberEntity> pages1 = workspaceMemberRepository.findWorkspaceMemberPage(workspace, PageRequest.of(1, pageSize));
        Page<WorkspaceMemberEntity> pages2 = workspaceMemberRepository.findWorkspaceMemberPage(workspace, PageRequest.of(2, pageSize));
        Page<WorkspaceMemberEntity> pages3 = workspaceMemberRepository.findWorkspaceMemberPage(workspace, PageRequest.of(3, pageSize));
        List<WorkspaceMemberEntity> result0 = pages0.getContent();
        List<WorkspaceMemberEntity> result1 = pages1.getContent();
        List<WorkspaceMemberEntity> result2 = pages2.getContent();
        List<WorkspaceMemberEntity> result3 = pages3.getContent();
        WorkspaceEntity workspace1 = result0.getFirst().getWorkspace();
        WorkspaceEntity workspace2 = result0.getFirst().getWorkspace();

        //then
        assertThat(result0).hasSize(pageSize);
        assertThat(result1).hasSize(pageSize);
        assertThat(result2).hasSize(pageSize);
        assertThat(result3).hasSize(pageSize - (repeat % 10));
        assertThat(workspace1).usingRecursiveComparison().isEqualTo(workspace2);

        assertThat(pages0.getTotalElements()).isEqualTo(35);
        assertThat(pages0.getNumber()).isEqualTo(0);
        assertThat(pages0.getTotalPages()).isEqualTo(4);
        assertThat(pages0.getNumberOfElements()).isEqualTo(10);
        assertThat(pages0.hasNext()).isTrue();
        assertThat(pages3.getNumberOfElements()).isEqualTo(5);
        assertThat(pages3.getNumber()).isEqualTo(3);
        assertThat(pages3.hasNext()).isFalse();
        assertThat(pages3.getSize()).isEqualTo(10);
    }

    private MemberEntity getMember(String email) {
        return new MemberEntity(
                "testNickname",
                email,
                "testPassword"
        );
    }


}
