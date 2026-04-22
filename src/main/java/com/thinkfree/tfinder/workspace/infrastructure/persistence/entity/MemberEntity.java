package com.thinkfree.tfinder.workspace.infrastructure.persistence.entity;

import com.thinkfree.tfinder.workspace.domain.Member;
import com.thinkfree.tfinder.workspace.domain.MemberType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = true)
    private String password;
    @Column(name = "member_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MemberType memberType;

    public MemberEntity(String name, String email, String password, MemberType memberType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.memberType = memberType;
    }

    public Member toDomain() {
        return new Member(
                this.id,
                this.name,
                this.email,
                this.password,
                this.memberType
        );
    }

    public static MemberEntity fromDomain(Member domain) {
        return new MemberEntity(
                domain.getId(),
                domain.getName(),
                domain.getEmail(),
                domain.getPassword(),
                domain.getUserType()
        );
    }

}
