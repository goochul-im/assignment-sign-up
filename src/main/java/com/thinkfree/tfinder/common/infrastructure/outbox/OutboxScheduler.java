package com.thinkfree.tfinder.common.infrastructure.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxEventProcessor outboxEventProcessor;

    @Scheduled(cron = "*/5 * * * * *") // 내부 처리가 길수도 있다
    //워커가 죽었을 때 어떻게?
    void schedule() {
        outboxEventProcessor.process();
    }


}
