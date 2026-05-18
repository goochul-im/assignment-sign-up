package com.thinkfree.tfinder.common.infrastructure.outbox.join_pending_invite;

import com.thinkfree.tfinder.common.infrastructure.outbox.iface.IOutboxPayloadMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
@RequiredArgsConstructor
public class JoinPendingInviteOutboxMapper implements IOutboxPayloadMapper<JoinPendingInvitePayload> {

    private final ObjectMapper objectMapper;

    @Override
    public String toPayload(JoinPendingInvitePayload event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JacksonException e) {
            //TODO: 로그만?
            log.error("JoinPendingInvitePayload를 직렬화하던 중 에러 발생, email : {}, memberId : {}, message = {}", event.email(),event.memberId(), e.getMessage());
            throw new IllegalArgumentException();
        }
    }

    @Override
    public JoinPendingInvitePayload fromPayload(String payload) {
        try {
            return objectMapper.readValue(payload, JoinPendingInvitePayload.class);
        } catch (JacksonException e) {
            //TODO: 로그만?
            log.error("JoinPendingInvitePayload를 직렬화하던 중 에러 발생, payload = {}, message = {}", payload, e.getMessage());
            throw new IllegalArgumentException();
        }
    }
}
