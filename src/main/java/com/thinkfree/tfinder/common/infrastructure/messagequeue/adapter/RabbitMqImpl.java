package com.thinkfree.tfinder.common.infrastructure.messagequeue.adapter;

import com.thinkfree.tfinder.common.config.RabbitMqConfig;
import com.thinkfree.tfinder.common.infrastructure.external.iface.IMailSender;
import com.thinkfree.tfinder.common.infrastructure.messagequeue.dto.InviteMessageDto;
import com.thinkfree.tfinder.common.infrastructure.messagequeue.dto.JoinWorkSpaceMessageDto;
import com.thinkfree.tfinder.common.infrastructure.messagequeue.dto.MessageDto;
import com.thinkfree.tfinder.common.infrastructure.messagequeue.handler.JoinWorkspaceHandler;
import com.thinkfree.tfinder.common.infrastructure.messagequeue.iface.IMessageQueue;
import com.thinkfree.tfinder.workspace.domain.MessageKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @deprecated 현재 사용되지 않음
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitMqImpl implements IMessageQueue { //TODO: 메시지 큐를 굳이 사용할 필요가 있나?

    private final RabbitTemplate rabbitTemplate;

    @Override
    public boolean publish(MessageKey key, MessageDto message) {

        try {
            rabbitTemplate.convertAndSend( // AmpqException 발생 가능
                    RabbitMqConfig.EXCHANGE_NAME,
                    key.getRoutingKey(),
                    message
            );
            log.info("메시지 송신, message ID = {}", message.getId());
        } catch (AmqpException e) {
            log.error("메시지가 큐에 정상적으로 저장되지 않았습니다. key = {}", key.getRoutingKey());
            return false;
        }

        return true;
    }

}
