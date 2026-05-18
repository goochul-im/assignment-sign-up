package com.thinkfree.tfinder.common.infrastructure.messagequeue;

import lombok.Getter;

/**
 * @deprecated 현재 사용되지 않음
 */
@Getter
public enum MessageKey {

    ;
    private final String routingKey;
    MessageKey(String key) {
        this.routingKey = key;
    }

}
