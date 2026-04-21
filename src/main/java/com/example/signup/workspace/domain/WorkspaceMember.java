package com.example.signup.workspace.domain;

import java.time.LocalDateTime;

public class WorkspaceMember {

    private Long id;
    private MemberRole role;
    private boolean isSaveSearchTerm;
    private LocalDateTime lastLoginTime;
    private Long workspaceId;
    private Long memberId;

}
