package com.thinkfree.tfinder.common.infrastructure.outbox;

import com.thinkfree.tfinder.common.infrastructure.outbox.enumrate.OutboxEventType;
import com.thinkfree.tfinder.common.infrastructure.outbox.iface.IOutboxEventHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OutboxEventHandlerProvider {

    private final Map<OutboxEventType, IOutboxEventHandler> handlers;

    public OutboxEventHandlerProvider(List<IOutboxEventHandler> handlerList) {
        handlers = new HashMap<>();
        for (IOutboxEventHandler handler : handlerList) {
            handlers.put(handler.supportType(), handler);
        }
    }

    public IOutboxEventHandler getHandler(OutboxEventType eventType) {
        IOutboxEventHandler handler = handlers.get(eventType);
        if (handler == null) {
            throw new IllegalArgumentException("지원하지 않는 outbox event type 입니다. eventType = " + eventType);
        }

        return handler;
    }
}
