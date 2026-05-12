package com.thinkfree.tfinder.common.infrastructure.persitence.adapter;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IEmailValidateRepository;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IPendingInviteRepository;
import com.thinkfree.tfinder.common.config.RabbitMqConfig;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.common.infrastructure.external.iface.IMailSender;
import com.thinkfree.tfinder.common.infrastructure.persitence.dto.InviteMessageDto;
import com.thinkfree.tfinder.common.infrastructure.persitence.dto.JoinWorkSpaceMessageDto;
import com.thinkfree.tfinder.common.infrastructure.persitence.dto.MessageDto;
import com.thinkfree.tfinder.common.infrastructure.persitence.iface.IMessageQueue;
import com.thinkfree.tfinder.workspace.domain.MessageKey;
import com.thinkfree.tfinder.workspace.domain.WorkspaceMemberRole;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IMemberRepository;
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
    private final IPendingInviteRepository pendingInviteRepository;
    private final IWorkspaceRepository workspaceRepository;
    private final IWorkspaceMemberRepository workspaceMemberRepository;
    private final IEmailValidateRepository emailValidateRepository;

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
        MemberEntity member = message.getMember();

        String email = member.getEmail();
        Set<String> pendingWorkspaceUrls = pendingInviteRepository.findWorkspaceUrlsByEmail(email);
        // redis에서 가져오는 걸 실패할떄는 어떻게 하지??
        // 아... 이거 쿼리가 너무 많이 나갈수 있겠는데... 나중에 bulk insert로 바꿔야 하나?

        for (String workspaceUrl : pendingWorkspaceUrls) {
            try {
                WorkspaceEntity workspace = workspaceRepository.findByWorkspaceUrl(workspaceUrl).orElseThrow(
                        () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND)
                );

                if (!workspaceMemberRepository.existsByWorkspaceAndMember(workspace, member)) {
                    workspaceMemberRepository.save(new WorkspaceMemberEntity(
                            workspace,
                            member,
                            WorkspaceMemberRole.MEMBER
                    ));
                }
            } catch (Exception e) {
                log.warn("참여 대기중인 워크스페이스에 참여 중 에러 발생, member = {}, workspaceUrl = {}", member.getEmail(), workspaceUrl);
            }
        }

        String signupEmail = member.getEmail();
        try {
            emailValidateRepository.delete(signupEmail);
            pendingInviteRepository.delete(signupEmail);
        } catch (Exception e) {
            log.warn("인증 정보 삭제중 에러 발생");
        }

    }


}
