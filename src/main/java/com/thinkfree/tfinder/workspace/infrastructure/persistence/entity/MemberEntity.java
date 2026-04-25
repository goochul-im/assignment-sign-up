package com.thinkfree.tfinder.workspace.infrastructure.persistence.entity;

import com.thinkfree.tfinder.workspace.domain.AuthProvider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) //PRIVATE가 안되는 이유? -> 프록시 객체는 super를 호출해야 하기 때문에
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
    private AuthProvider authProvider;
    @Column // 추후 소셜 로그인 구현 시 추가 예정
    private String oauthId;

    public MemberEntity(String username, String email, String password, AuthProvider authProvider) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.authProvider = authProvider;
        oauthId = null;
    }

}
