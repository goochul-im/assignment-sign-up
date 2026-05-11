package com.thinkfree.tfinder.common.infrastructure.persitence.iface;

import com.thinkfree.tfinder.common.infrastructure.persitence.dto.MessageDto;
import com.thinkfree.tfinder.workspace.domain.MessageKey;

public interface IMessageQueue {

    boolean publish(MessageKey key, MessageDto message);

}
