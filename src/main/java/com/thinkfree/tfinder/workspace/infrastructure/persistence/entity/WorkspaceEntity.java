package com.thinkfree.tfinder.workspace.infrastructure.persistence.entity;

import com.thinkfree.tfinder.workspace.domain.Workspace;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 엔티티에는 아무것도 없는 생성자가 하나 있어야 하지만 이 생성자를 아무나 써서는 안되기 때문에 추가
public class WorkspaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, unique = true)
    private String url;
    @Column(name = "remain_message_count")
    private Long remainMessageCount = 0L;
    @Column
    private boolean isDelete = false;

    public WorkspaceEntity(String name, String url) {
        this.name = name;
        this.url = url;
        // remainMessageCount랑 isDelete가 없는 이유?
        // 생성자를 이용하는 경우는 엔티티를 만들 떄는 새로 워크스페이스를 만드는 경우임
        // 워크스페이스가 새로 만들어질 때 remainMessageCount와 isDelete는 기본값이 있어야 함
    }

    public Workspace toDomain() {
        return new Workspace(
                this.id,
                this.name,
                this.url,
                this.remainMessageCount,
                this.isDelete
        );
    }

    public static WorkspaceEntity fromDomain(Workspace domain) {
        return new WorkspaceEntity(
                domain.getId(),
                domain.getWorkspaceName(),
                domain.getWorkspaceUrl(),
                domain.getRemainMessageCount(),
                domain.isDelete()
        );
    }

}
