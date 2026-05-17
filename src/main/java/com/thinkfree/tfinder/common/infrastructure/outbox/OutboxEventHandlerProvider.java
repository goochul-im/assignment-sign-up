package com.thinkfree.tfinder.common.infrastructure.outbox;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OutboxEventHandlerProvider {

    private final Map<OutboxEventType, OutboxEventHandler> handlers;

    public OutboxEventHandlerProvider(List<OutboxEventHandler> handlerList) {
        handlers = new HashMap<>();
        for (OutboxEventHandler handler : handlerList) {
            handlers.put(handler.supportType(), handler);
        }
    }

    public OutboxEventHandler getHandler(OutboxEventType eventType) {
        OutboxEventHandler handler = handlers.get(eventType);
        if (handler == null) {
            throw new IllegalArgumentException("지원하지 않는 outbox event type 입니다. eventType = " + eventType);
        }

        return handler;
    }
}
