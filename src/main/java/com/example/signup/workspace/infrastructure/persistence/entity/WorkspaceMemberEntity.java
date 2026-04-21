package com.example.signup.workspace.infrastructure.persistence.entity;

import com.example.signup.workspace.domain.MemberRole;
import com.example.signup.workspace.domain.WorkspaceMember;
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

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MemberRole role;
    @Column(nullable = false)
    private boolean isSaveSearchTerm;
    @Column()
    private Instant lastLoginTime; // DB 저장시 UTC로 표준화
    @Column(nullable = false)
    private Long workspaceId;
    @Column(nullable = false)
    private Long memberId; // 왜 다른 엔티티 클래스를 쓰지 않고 id를 직접 넣었는가

    public WorkspaceMemberEntity(MemberRole role, boolean isSaveSearchTerm, Instant lastLoginTime, Long workspaceId, Long memberId) {
        this.role = role;
        this.isSaveSearchTerm = isSaveSearchTerm;
        this.lastLoginTime = lastLoginTime;
        this.workspaceId = workspaceId;
        this.memberId = memberId;
    }

    public WorkspaceMember toDomain() {
        return new WorkspaceMember(
                this.id,
                this.workspaceId,
                this.memberId,
                this.role,
                this.isSaveSearchTerm,
                this.lastLoginTime
        );
    }

    public static WorkspaceMemberEntity fromDomain(WorkspaceMember domain) {
        return new WorkspaceMemberEntity(
                domain.getId(),
                domain.getRole(),
                domain.isSaveSearchTerm(),
                domain.getLastLoginTime(),
                domain.getWorkspaceId(),
                domain.getMemberId()
        );
    }

}
