package com.thinkfree.tfinder.common.infrastructure.outbox;

import com.thinkfree.tfinder.common.infrastructure.outbox.iface.IOutboxEventHandler;
import com.thinkfree.tfinder.common.infrastructure.outbox.iface.IOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventProducer {

    private static final int BATCH_SIZE = 100;

    private final IOutboxRepository outboxRepository;
    private final OutboxEventHandlerProvider handlerProvider;

    @Transactional
    public void process() {
        List<OutboxEntity> outboxes = outboxRepository.findPendingForUpdate(BATCH_SIZE);

        for (OutboxEntity outbox : outboxes) {
            try {
                IOutboxEventHandler handler = handlerProvider.getHandler(outbox.getEventType());
                handler.handle(outbox);
                outbox.markDone();
            } catch (Exception e) {
                outbox.addRetryCount();
                log.warn("outbox 처리 실패. outboxId = {}, eventType = {}, retryCount = {}",
                        outbox.getId(),
                        outbox.getEventType(),
                        outbox.getRetryCount(),
                        e
                );
            }
        }
    }
}
