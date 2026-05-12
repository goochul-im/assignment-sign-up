package com.thinkfree.tfinder.common.infrastructure.messagequeue.dto;

import lombok.Getter;

import java.time.Instant;

@Getter
public abstract class MessageDto {

    private final String id;
    private final Instant timestamp;

    protected MessageDto(String id) {
        this.id = id;
        this.timestamp = Instant.now();
    }

}
