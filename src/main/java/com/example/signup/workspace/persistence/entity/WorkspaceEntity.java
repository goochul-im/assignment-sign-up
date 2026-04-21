package com.example.signup.workspace.persistence.entity;

import com.example.signup.workspace.domain.Workspace;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 엔티티에는 아무것도 없는 생성자가 하나 있어야 하지만 이 생성자를 아무나 써서는 안되기 때문에 추가
public class WorkspaceEntity {

    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String name;
    @Column
    private String address;
    @Column(name = "remain_message_count")
    private Long remainMessageCount;
    @Column
    private boolean isDelete;

    public WorkspaceEntity(String name, String address, Long remainMessageCount, boolean isDelete) {
        this.name = name;
        this.address = address;
        this.remainMessageCount = remainMessageCount;
        this.isDelete = isDelete;
    }

    public Workspace toDomain() {
        return new Workspace(
                this.id,
                this.name,
                this.address,
                this.remainMessageCount,
                this.isDelete
        );
    }

    public static WorkspaceEntity fromDomain(Workspace domain) {
        return new WorkspaceEntity(
                domain.getId(),
                domain.getWorkspaceName(),
                domain.getAddress(),
                domain.getRemainMessageCount(),
                domain.isDelete()
        );
    }

}
