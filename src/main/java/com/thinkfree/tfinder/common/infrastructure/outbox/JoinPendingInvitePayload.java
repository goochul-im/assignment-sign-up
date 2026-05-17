package com.thinkfree.tfinder.common.infrastructure.outbox;

public record JoinPendingInvitePayload(
        Long memberId,
        String email
) {
}
