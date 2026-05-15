package com.thinkfree.tfinder.common.infrastructure.messagequeue.iface;

import com.thinkfree.tfinder.common.infrastructure.messagequeue.dto.MessageDto;
import com.thinkfree.tfinder.workspace.domain.MessageKey;

/**
 * no use not
 * @deprecated 현재 사용하지 않음. 미래에 사용 가능성 있음
 */
public interface IMessageQueue {

    boolean publish(MessageKey key, MessageDto message);

}
