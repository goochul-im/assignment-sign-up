package com.thinkfree.tfinder.workspace.infrastructure.external.adpater;

import com.thinkfree.tfinder.workspace.infrastructure.external.iface.IMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleMailSender implements IMailSender {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEMail;

    @Override
    public void send(String toEmail, String title, String message) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setText(message);
            helper.setFrom(fromEMail);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e); // TODO: 수정 필요
        }

        log.info("{} 로 전송 완료", toEmail);
    }

}
