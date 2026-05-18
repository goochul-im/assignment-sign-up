package com.thinkfree.tfinder.common.infrastructure.outbox.iface;

import com.thinkfree.tfinder.common.infrastructure.outbox.enumrate.OutboxEventType;

public interface IOutboxPayloadMapper<T> {

    /**
     * 이벤트를 DB에 저장하기 위한 페이로드로 변환합니다
     * @param event 페이로드로 변환할 이벤트
     * @return 변환된 페이로드
     */
    String toPayload(T event);

    /**
     * 페이로드를 이벤트로 변환합니다
     * @param payload 이벤트로 변환할 페이로드
     * @return 이벤트
     */
    T fromPayload(String payload);

}
