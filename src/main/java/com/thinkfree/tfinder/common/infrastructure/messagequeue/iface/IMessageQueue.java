package com.thinkfree.tfinder.common.infrastructure.messagequeue.iface;

import com.thinkfree.tfinder.common.infrastructure.messagequeue.dto.MessageDto;
import com.thinkfree.tfinder.workspace.domain.MessageKey;

public interface IMessageQueue {

    boolean publish(MessageKey key, MessageDto message);

}
