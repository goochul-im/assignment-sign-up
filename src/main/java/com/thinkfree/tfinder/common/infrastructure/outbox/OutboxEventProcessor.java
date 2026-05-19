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

    private static final int BATCH_SIZE = 100; // 이벤트를 몇개씩 가져올건가?

    private final IOutboxRepository outboxRepository;
    private final OutboxEventHandlerProvider handlerProvider;

    // 커넥션을 오래 잡아서 생기는 문제 있음
    public void process() {
        List<OutboxEventEntity> outboxes = outboxRepository.findPendingForUpdate(BATCH_SIZE);

        for (OutboxEventEntity outbox : outboxes) {
            try {
                IOutboxEventHandler handler = handlerProvider.getHandler(outbox.getEventType());
                handler.handle(outbox);
                outbox.markDone();
            } catch (Exception e) {
                // 여기서도 예외가 터지면??
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
