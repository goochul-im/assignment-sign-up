package com.thinkfree.tfinder.workspace.infrastructure.external.iface;

public interface IMailSender {

    void send(String toEmail, String title ,String message);

}
