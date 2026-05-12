package com.thinkfree.tfinder.common.infrastructure.event;

import com.thinkfree.tfinder.common.infrastructure.messagequeue.dto.JoinWorkSpaceMessageDto;
import com.thinkfree.tfinder.common.infrastructure.messagequeue.iface.IMessageQueue;
import com.thinkfree.tfinder.workspace.domain.MessageKey;
import com.thinkfree.tfinder.workspace.event.JoinPendingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventListener {

    private final IMessageQueue messageQueue;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(JoinPendingEvent event) {
        log.info("join 트랜잭션 이벤트 수신 완료");
        messageQueue.publish(MessageKey.JOIN_WORKSPACE, new JoinWorkSpaceMessageDto(
                UUID.randomUUID().toString(), // ID를 뭘로 할까?
                event.member()
        ));
    }

}
