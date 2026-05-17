package com.thinkfree.tfinder.common.infrastructure.outbox;

import lombok.Getter;

@Getter
public enum OutboxEventType {
    JOIN_WORKSPACE_PENDING_INVITE(5)
    ;

    private final int maxRetryCount;

    OutboxEventType(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }
}
