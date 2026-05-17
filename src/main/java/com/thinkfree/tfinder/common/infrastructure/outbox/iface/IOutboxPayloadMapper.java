package com.thinkfree.tfinder.common.infrastructure.outbox.iface;

import com.thinkfree.tfinder.common.infrastructure.outbox.enumrate.OutboxEventType;

public interface IOutboxPayloadMapper<T> {

    OutboxEventType supportType();

    String toPayload(T event);

    T fromPayload(String payload);

}
