package com.example.signup.workspace.persistence.entity;

import com.example.signup.workspace.domain.MemberRole;
import com.example.signup.workspace.domain.WorkspaceMember;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
public class WorkspaceMemberEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column()
    @Enumerated(value = EnumType.STRING)
    private MemberRole role;
    @Column
    private boolean isSaveSearchTerm;
    @Column
    private LocalDateTime lastLoginTime;
    @Column
    private Long workspaceId;
    @Column
    private Long memberId;

    public WorkspaceMemberEntity(MemberRole role, boolean isSaveSearchTerm, LocalDateTime lastLoginTime, Long workspaceId, Long memberId) {
        this.role = role;
        this.isSaveSearchTerm = isSaveSearchTerm;
        this.lastLoginTime = lastLoginTime;
        this.workspaceId = workspaceId;
        this.memberId = memberId;
    }

    public WorkspaceMember toDomain() {
        return new WorkspaceMember(
                this.id,
                this.role,
                this.isSaveSearchTerm,
                this.lastLoginTime,
                this.workspaceId,
                this.memberId
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
