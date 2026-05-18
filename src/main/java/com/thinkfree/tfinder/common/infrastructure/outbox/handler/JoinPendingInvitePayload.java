package com.thinkfree.tfinder.common.infrastructure.outbox.handler;

public record JoinPendingInvitePayload(
        Long memberId,
        String email
) {
}
