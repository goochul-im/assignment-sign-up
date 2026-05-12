package com.thinkfree.tfinder.common.infrastructure.messagequeue.dto;

import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import lombok.Getter;

@Getter
public class JoinWorkSpaceMessageDto extends MessageDto{

    private final MemberEntity member;

    public JoinWorkSpaceMessageDto(String id, MemberEntity member) {
        super(id);
        this.member = member;
    }

}
