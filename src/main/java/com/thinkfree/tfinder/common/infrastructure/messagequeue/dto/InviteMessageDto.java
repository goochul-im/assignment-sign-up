package com.thinkfree.tfinder.common.infrastructure.messagequeue.dto;

import lombok.Getter;

@Getter
public class InviteMessageDto extends MessageDto{

    private final String toEmail;
    private final String title;
    private final String message;

    public InviteMessageDto(String id, String toEmail, String title, String message) {
        super(id);
        this.toEmail = toEmail;
        this.title = title;
        this.message = message;
    }
}
