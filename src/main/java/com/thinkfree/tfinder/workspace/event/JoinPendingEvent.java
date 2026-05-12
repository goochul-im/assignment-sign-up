package com.thinkfree.tfinder.workspace.event;

import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;

public record JoinPendingEvent(
        MemberEntity member
) {
}
