package com.thinkfree.tfinder.workspace.infrastructure.persistence.entity;

import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity(name = "workspace_member")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkspaceMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY를 사용하여 EAGER 타입 사용시 불필요한 join과 jpql에서의 N+1 문제 방지 (jpql에서는 fetch join 쓰자)
    @JoinColumn(nullable = false, name = "workspace_id")
    private WorkspaceEntity workspace;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "member_id")
    private MemberEntity member;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private WorkspaceMemberRole role;

    public WorkspaceMemberEntity(
            WorkspaceEntity workspace,
            MemberEntity member,
            WorkspaceMemberRole role) {
        this.workspace = workspace;
        this.member = member;
        this.role = role;
    }
}
