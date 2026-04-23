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

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private WorkspaceMemberRole role;
    @Column(nullable = false)
    private boolean isSaveSearchTerm;
    @Column()
    private Instant lastLoginTime; // DB 저장시 Timestamp로 저장됨, TZ가 있음.
    @Column(nullable = false)
    private Long workspaceId;
    @Column(nullable = false)
    private Long memberId; // 왜 다른 엔티티 클래스를 쓰지 않고 id를 직접 넣었는가

    public WorkspaceMemberEntity(WorkspaceMemberRole role, boolean isSaveSearchTerm, Instant lastLoginTime, Long workspaceId, Long memberId) {
        this.role = role;
        this.isSaveSearchTerm = isSaveSearchTerm;
        this.lastLoginTime = lastLoginTime;
        this.workspaceId = workspaceId;
        this.memberId = memberId;
    }

}
