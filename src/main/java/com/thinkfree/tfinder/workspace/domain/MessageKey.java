package com.thinkfree.tfinder.workspace.domain;

/**
 * @deprecated 현재 사용되지 않음
 */
public enum MessageKey {

    INVITE("email.invite.key"),
    JOIN_WORKSPACE("join.workspace.key");

    private final String routingKey;
    MessageKey(String key) {
        this.routingKey = key;
    }

    public String getRoutingKey() {
        return routingKey;
    }

}
