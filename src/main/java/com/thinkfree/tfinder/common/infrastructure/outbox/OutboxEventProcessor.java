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
public class OutboxEventProcessor {

    private static final int BATCH_SIZE = 100;

    private final IOutboxRepository outboxRepository;
    private final OutboxEventHandlerProvider handlerProvider;

    @Transactional
    public void process() {
        List<OutboxEventEntity> outboxes = outboxRepository.findPendingForUpdate(BATCH_SIZE);

        for (OutboxEventEntity outbox : outboxes) {
            try {
                IOutboxEventHandler handler = handlerProvider.getHandler(outbox.getEventType());
                handler.handle(outbox);
                outbox.markDone();
            } catch (Exception e) { // TODO: 여기서 catch하고 있는데 왜 바깥까지 예외가 던져지지?
                outbox.addRetryCount();
                log.warn("outbox 처리 실패. outboxId = {}, eventType = {}, retryCount = {}, message = {}",
                        outbox.getId(),
                        outbox.getEventType(),
                        outbox.getRetryCount(),
                        e.getMessage()
                );
            }
        }
    }
}
