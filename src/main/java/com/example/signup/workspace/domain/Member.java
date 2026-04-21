package com.example.signup.workspace.domain;

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

}
