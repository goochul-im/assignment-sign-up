package com.thinkfree.tfinder.common.infrastructure.outbox;

public interface OutboxPayloadMapper<T> {

    OutboxEventType supportType();

    String toPayload(T event);

    T fromPayload(String payload);

}
