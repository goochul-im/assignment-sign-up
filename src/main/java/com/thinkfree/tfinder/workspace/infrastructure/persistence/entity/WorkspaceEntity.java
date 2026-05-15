package com.thinkfree.tfinder.workspace.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;

@Entity(name = "workspace")
@Getter
@AllArgsConstructor
@SQLRestriction("is_delete = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 엔티티에는 아무것도 없는 생성자가 하나 있어야 하지만 이 생성자를 아무나 써서는 안되기 때문에 추가
public class WorkspaceEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String workspaceName;
    @Column(nullable = false, unique = true)
    private String workspaceUrl;
    @Column(name = "remain_message_amount", nullable = false)
    private Long remainMessageAmount = 0L;
    @Column(name = "is_delete", nullable = false)
    private boolean isDelete = false;
    @Column(name = "deleted_at")
    private Instant deletedAt = null;

    public WorkspaceEntity(String workspaceName, String workspaceUrl) {
        this.workspaceName = workspaceName;
        this.workspaceUrl = workspaceUrl;
    }

    public void delete() {
        this.isDelete = true;
        this.deletedAt = Instant.now();
    }

}
