package com.thinkfree.tfinder.common.infrastructure.external.adapter;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IMailSendLimitRepository;
import com.thinkfree.tfinder.common.infrastructure.external.iface.IMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("local")
public class GoogleMailSender implements IMailSender {

    private final JavaMailSender mailSender;
    private final IMailSendLimitRepository emailSendLimitRepository;
    @Value("${spring.mail.username}")
    private String fromEMail;

    @Override
    @Async
    public void asyncSend(String toEmail, String title, String message) {
        send(toEmail, title, message);
    }

    @Override
    @Async
    public void asyncSend(String toEmail, String title, String message, long workspaceId) { send(toEmail, title, message, workspaceId); }

    private void send(String toEmail, String title, String message, long workspaceId) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            doSend(toEmail, title, message, mimeMessage);
        } catch (MailAuthenticationException e) {
            log.error("SMTP 서버 인증 정보에 에러가 발생했습니다.");
            emailSendLimitRepository.increaseRemainLimit(1, workspaceId);
        } catch (MessagingException e) {
            log.warn("메일을 보내는 중 에러 발생. toEmailList : {}, message : {}", toEmail, e.getMessage());
            emailSendLimitRepository.increaseRemainLimit(1, workspaceId);
        }
    }

    public void send(String toEmail, String title, String message) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            doSend(toEmail, title, message, mimeMessage);
        } catch (MailAuthenticationException e) {
            log.error("SMTP 서버 인증 정보에 에러가 발생했습니다.");
        } catch (MessagingException e) {
            log.warn("메일을 보내는 중 에러 발생. toEmailList : {}, message : {}", toEmail, e.getMessage());
        }

    }

    private void doSend(String toEmail, String title, String message, MimeMessage mimeMessage) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
        helper.setTo(toEmail);
        helper.setSubject(title);
        helper.setText(message, true);
        helper.setFrom(fromEMail);

        mailSender.send(mimeMessage);
        log.info("{} 로 전송 완료", toEmail);
    }

}
