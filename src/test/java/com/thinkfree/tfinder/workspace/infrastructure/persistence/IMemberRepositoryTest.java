package com.thinkfree.tfinder.workspace.infrastructure.persistence;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.thinkfree.tfinder.annotation.IntegrationTest;
import com.thinkfree.tfinder.workspace.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@IntegrationTest
@ActiveProfiles("test")
class IMemberRepositoryTest {

    @Autowired
    private IWorkspaceMemberRepository workspaceMemberRepository;
    @Autowired
    private IMemberRepository memberRepository;
    @Autowired
    private IWorkspaceRepository workspaceRepository;

    private final FixtureMonkey fixture = FixtureMonkey.builder()
            .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
            .build();

    @Test
    void 이메일_리스트_중_워크스페이스에_속한_이메일만_가져올_수_있다(){
        //given
        String email1 = "test1@email.com";
        String email2 = "test2@email.com";
        String email3 = "test3@email.com";
        List<String> emails = new ArrayList<>();
        emails.add(email1);
        emails.add(email2);
        emails.add(email3);
        for (int i = 4; i < 16; i++) {
            emails.add("test" + i + "@email.com");
        }

        WorkspaceEntity workspace = new WorkspaceEntity(
                "workspaceName",
                "workspaceUrl"
        );
        WorkspaceEntity anotherWorkspace = new WorkspaceEntity(
                "anotherWorkspaceName",
                "anotherWorkspaceUrl"
        );
        workspace = workspaceRepository.save(workspace);
        anotherWorkspace = workspaceRepository.save(anotherWorkspace);
        MemberEntity member1 = getMember(email1);
        MemberEntity member2 = getMember(email2);
        MemberEntity member3 = getMember(email3);
        MemberEntity member4 = getMember("anotherMember");
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        workspaceMemberRepository.save(new WorkspaceMemberEntity(workspace, member1, WorkspaceMemberRole.MEMBER));
        workspaceMemberRepository.save(new WorkspaceMemberEntity(workspace, member2, WorkspaceMemberRole.MEMBER));
        workspaceMemberRepository.save(new WorkspaceMemberEntity(workspace, member3, WorkspaceMemberRole.MEMBER));
        workspaceMemberRepository.save(new WorkspaceMemberEntity(anotherWorkspace, member4, WorkspaceMemberRole.MEMBER));

        //when
        Set<String> result = memberRepository.findJoinedEmails(workspace, emails);

        //then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(email1, email2, email3);
    }

    @Test
    void 이메일에_null_값이_들어오면_false를_반환한다(){
        //given
        String email = null;

        //when
        boolean b = memberRepository.existsByEmail(email);

        //then
        assertThat(b).isFalse();
    }

    private MemberEntity getMember(String email) {
        return new MemberEntity(
                "testNickname",
                email,
                "testPassword"
        );
    }

}
