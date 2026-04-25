package com.thinkfree.tfinder.workspace.infrastructure.external.adpater;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.workspace.infrastructure.external.iface.IMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleMailSender implements IMailSender {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEMail;

    @Override
    @Async
    public void asyncSend(String toEmail, String title, String message) {
        send(toEmail, title, message);
    }

    @Override
    public void send(String toEmail, String title, String message) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setText(message, true);
            helper.setFrom(fromEMail);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.warn("메일을 보내는 중 에러 발생. toEmail : {}, message : {}", toEmail, e.getMessage());
//            throw new BusinessException(ErrorCode.MAIL_SEND_ERROR);
        }

        log.info("{} 로 전송 완료", toEmail);
    }

}
