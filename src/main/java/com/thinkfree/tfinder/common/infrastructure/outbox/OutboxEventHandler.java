package com.thinkfree.tfinder.common.infrastructure.outbox;

public interface OutboxEventHandler {

    OutboxEventType supportType();

    void handle(OutboxEntity outbox);

}
