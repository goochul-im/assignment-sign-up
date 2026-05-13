package com.thinkfree.tfinder.common.infrastructure.external.adapter;

import com.thinkfree.tfinder.common.infrastructure.external.iface.IMailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("local")
public class EmptyMailSender implements IMailSender {

    @Override
    public void asyncSend(String toEmail, String title, String message) {
        log.info("async send");
    }

    @Override
    public void send(String toEmail, String title, String message) {
        log.info("sync send");
    }
}
