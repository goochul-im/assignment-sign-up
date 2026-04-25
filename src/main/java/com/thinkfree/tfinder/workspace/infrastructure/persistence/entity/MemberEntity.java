package com.thinkfree.tfinder.workspace.infrastructure.persistence.entity;

import com.thinkfree.tfinder.workspace.domain.MemberType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) //PRIVATE가 안되는 이유? -> 프록시 객체는 super를 호출해야 하기 때문에
@Entity
@Getter
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = true)
    private String password;
    @Column(name = "member_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MemberType memberType;

    public MemberEntity(String username, String email, String password, MemberType memberType) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.memberType = memberType;
    }

}
