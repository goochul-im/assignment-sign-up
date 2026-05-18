package com.thinkfree.tfinder.workspace.domain;

import lombok.Getter;

/**
 * 임시로 구독 플랜 대신에 사용되는 enum입니다.
 * 추후 구독 플랜 기능이 작성되면 삭제되어야합니다.
 */
@Getter
public enum WorkspaceTier {

    FREE(10,5), PRO(50, 30), MAX(150, 100)
    ;

    private final int mailLimit;
    private final int capacity;

    WorkspaceTier(int mailLimit, int capacity) {
        this.mailLimit = mailLimit;
        this.capacity = capacity;
    }

}
