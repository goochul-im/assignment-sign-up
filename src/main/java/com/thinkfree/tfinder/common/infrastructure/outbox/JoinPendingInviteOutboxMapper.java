package com.thinkfree.tfinder.common.infrastructure.outbox;

import com.thinkfree.tfinder.common.infrastructure.outbox.enumrate.OutboxEventType;
import com.thinkfree.tfinder.common.infrastructure.outbox.iface.IOutboxPayloadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class JoinPendingInviteOutboxMapper implements IOutboxPayloadMapper<JoinPendingInvitePayload> {

    private final ObjectMapper objectMapper;

    @Override
    public OutboxEventType supportType() {
        return OutboxEventType.JOIN_WORKSPACE_PENDING_INVITE;
    }

    @Override
    public String toPayload(JoinPendingInvitePayload event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JacksonException e) {
            //TODO: 로그만?
            throw new IllegalArgumentException();
        }
    }

    @Override
    public JoinPendingInvitePayload fromPayload(String payload) {
        try {
            return objectMapper.readValue(payload, JoinPendingInvitePayload.class);
        } catch (JacksonException e) {
            //TODO: 로그만?
            throw new IllegalArgumentException();
        }
    }
}
