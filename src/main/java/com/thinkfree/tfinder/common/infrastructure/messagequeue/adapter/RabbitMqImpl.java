package com.thinkfree.tfinder.common.infrastructure.messagequeue.adapter;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IEmailValidateRepository;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IPendingInviteRepository;
import com.thinkfree.tfinder.common.config.RabbitMqConfig;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.common.infrastructure.external.iface.IMailSender;
import com.thinkfree.tfinder.common.infrastructure.messagequeue.dto.InviteMessageDto;
import com.thinkfree.tfinder.common.infrastructure.messagequeue.dto.JoinWorkSpaceMessageDto;
import com.thinkfree.tfinder.common.infrastructure.messagequeue.dto.MessageDto;
import com.thinkfree.tfinder.common.infrastructure.messagequeue.handler.JoinWorkspaceHandler;
import com.thinkfree.tfinder.common.infrastructure.messagequeue.iface.IMessageQueue;
import com.thinkfree.tfinder.workspace.domain.MessageKey;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IWorkspaceRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceEntity;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.WorkspaceMemberEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitMqImpl implements IMessageQueue {

    private final RabbitTemplate rabbitTemplate;
    private final IMailSender iMailSender;
    private final JoinWorkspaceHandler joinWorkspaceHandler;


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

    /**
     * 메시지 큐 방식으로 바꾸기는 했지만, 한번에 소비되는 메시지 수를 제한하지 않으면 무슨 소용인가?
     * 비동기 전송 대신에 동기식 전송으로 바꾸자.
     */
    @RabbitListener(queues = RabbitMqConfig.INVITE_QUEUE_NAME)
    public void inviteMessageConsume(InviteMessageDto message) {
        log.info("메시지 수신, message ID = {}", message.getId());
        iMailSender.send(message.getToEmail(), message.getTitle(), message.getMessage());
    }

    /**
     * 핸들러로 변경할까?
     */
    @RabbitListener(queues = RabbitMqConfig.JOIN_QUEUE_NAME)
    public void joinMessageConsume(JoinWorkSpaceMessageDto message) {
        log.info("join 메시지 수신, message ID = {}", message.getId());
        joinWorkspaceHandler.handler(message);
    }


}
