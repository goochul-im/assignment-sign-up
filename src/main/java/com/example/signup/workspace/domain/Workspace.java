package com.example.signup.workspace.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Workspace {

    private Long id;
    private String workspaceName;
    private String address;
    private Long remainMessageCount;
    private boolean isDelete;

}
