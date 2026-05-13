package com.thinkfree.tfinder.testacomponent;

import com.thinkfree.tfinder.common.infrastructure.external.iface.IMailSender;
import org.springframework.context.annotation.Profile;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Profile({"test"})
public class TestMailSender implements IMailSender {

    @Override
    public void asyncSend(String toEmail, String title, String message) {

    }

    @Override
    public void send(String toEmail, String title, String message) {

        RetryTemplate retryTemplate = new RetryTemplate(RetryPolicy.builder()
                .includes(RuntimeException.class)
                .maxRetries(4)
                .delay(Duration.ofMillis(100))
                .maxDelay(Duration.ofMillis(1000))
                .build());

        try {
            retryTemplate.invoke(() -> {
                run();
                try {
                    exe();
                } catch (Exception e) {
                    System.out.println("Exception catch");
                    return;
                }
                System.out.println("invoke end");
            });

        } catch (RuntimeException e) {
            throw new MailSendException("재시도 실패", e);
        }
    }

    private void run() {
        System.out.println("run called");
        throw new RuntimeException();
    }

    private void exe() throws Exception{
        System.out.println("exe called");
        throw new Exception("exception");
    }

}
