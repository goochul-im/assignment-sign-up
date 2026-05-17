package com.thinkfree.tfinder.common.infrastructure.outbox.iface;

import com.thinkfree.tfinder.common.infrastructure.outbox.OutboxEntity;
import com.thinkfree.tfinder.common.infrastructure.outbox.enumrate.OutboxEventType;

public interface IOutboxEventHandler {

    OutboxEventType supportType();

    void handle(OutboxEntity outbox);

}
