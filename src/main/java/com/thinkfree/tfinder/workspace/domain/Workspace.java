package com.thinkfree.tfinder.workspace.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Workspace {

    private Long id;
    private String workspaceName; // 얘도 변경되면 어떡할래
    private String workspaceUrl;
    private Long remainMessageCount; // 여러 파드에서 사용한다면 문제가 생김, 데이터베이스에서 바로 사용하는게 좋음
    private boolean isDelete; // 이것도 마찬가지

}
