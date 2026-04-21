package com.example.signup.workspace.infrastructure.external.iface;

public interface IMailSender {

    void send(String toEmail, String title ,String message);

}
