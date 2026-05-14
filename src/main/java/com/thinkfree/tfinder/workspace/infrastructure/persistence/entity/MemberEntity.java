package com.thinkfree.tfinder.workspace.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "member")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) //PRIVATE가 안되는 이유? -> 프록시 객체는 super를 호출해야 하기 때문에
public class MemberEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nickname;
    @Column(nullable = false, unique = true)
    private String email;
    @Column()
    private String password;
    // OAuthAccount 추가 필요

    public MemberEntity(String nickname, String email, String password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

}
