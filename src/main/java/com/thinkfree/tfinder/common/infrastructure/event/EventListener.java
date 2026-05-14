package com.thinkfree.tfinder.common.infrastructure.event;

import com.thinkfree.tfinder.common.infrastructure.messagequeue.handler.JoinWorkspaceHandler;
import com.thinkfree.tfinder.workspace.event.JoinPendingEvent;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventListener {

    private final JoinWorkspaceHandler handler;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void joinPendingInviteListener(JoinPendingEvent event) {
        log.info("join 트랜잭션 이벤트 수신 완료");
        MemberEntity member = event.member();
        handler.handle(member);
    }

}
