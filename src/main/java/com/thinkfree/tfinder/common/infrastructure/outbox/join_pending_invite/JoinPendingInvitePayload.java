package com.thinkfree.tfinder.common.infrastructure.outbox.join_pending_invite;

public record JoinPendingInvitePayload(
        Long memberId,
        String email
) {
}
