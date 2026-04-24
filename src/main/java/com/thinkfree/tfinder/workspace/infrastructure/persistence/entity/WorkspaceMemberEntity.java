package com.thinkfree.tfinder.workspace.infrastructure.persistence.entity;

import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
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
    @Column()
    private Instant lastLoginTime; // DB 저장시 Timestamp로 저장됨, TZ가 있음.
     // 왜 다른 엔티티 클래스를 쓰지 않고 id를 직접 넣었는가


    public WorkspaceMemberEntity(WorkspaceEntity workspace, MemberEntity member, WorkspaceMemberRole role, Instant lastLoginTime) {
        this.workspace = workspace;
        this.member = member;
        this.role = role;
        this.lastLoginTime = lastLoginTime;
    }
}
