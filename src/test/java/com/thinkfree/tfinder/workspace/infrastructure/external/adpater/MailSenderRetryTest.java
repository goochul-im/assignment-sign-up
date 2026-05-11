package com.thinkfree.tfinder.workspace.infrastructure.external.adpater;

import com.thinkfree.tfinder.annotation.IntegrationTest;
import com.thinkfree.tfinder.common.infrastructure.external.iface.IMailSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailSendException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@IntegrationTest
class MailSenderRetryTest {

    @Autowired
    private IMailSender mailSender;

    @Test
    void Retryable_어노테이션으로_재시도를_할_수_있다(){
        //given & when
        assertThrows(MailSendException.class, () -> mailSender.send("to", "title", "message"));

    }


}
