package com.thinkfree.tfinder.common.infrastructure.outbox.iface;

import com.thinkfree.tfinder.common.infrastructure.outbox.OutboxEntity;
import com.thinkfree.tfinder.common.infrastructure.outbox.enumrate.OutboxEventType;

public interface IOutboxEventHandler {

    /**
     * 핸들러가 지원하는 이벤트 타입을 반환합니다
     * @return 지원 이벤트 타입
     */
    OutboxEventType supportType();

    /**
     * 핸들러에 정의된 로직을 실행합니다
     * @param outbox 핸들링할 outbox
     */
    void handle(OutboxEntity outbox);

}
