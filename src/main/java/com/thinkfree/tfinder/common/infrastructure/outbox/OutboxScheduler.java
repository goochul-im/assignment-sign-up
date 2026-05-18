package com.thinkfree.tfinder.common.infrastructure.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxEventProcessor outboxEventProcessor;

    @Scheduled(cron = "*/5 * * * * *")
    void schedule() {
        outboxEventProcessor.process();
    }


}
