package com.example.signup.workspace.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class WorkspaceMember {

    private Long id;
    private MemberRole role;
    private boolean isSaveSearchTerm;
    private Instant lastLoginTime;
    private Long workspaceId;
    private Long memberId;

}
