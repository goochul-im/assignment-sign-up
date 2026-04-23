package com.thinkfree.tfinder.workspace.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Member {

    private Long id;
    private String name;
    private String email;
    private String password;
    private MemberType userType;

    public Member(String name, String email, String password, MemberType userType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }
}
