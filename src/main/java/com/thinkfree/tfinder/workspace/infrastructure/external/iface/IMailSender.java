package com.thinkfree.tfinder.workspace.infrastructure.external.iface;

public interface IMailSender {

    /**
     * 메일을 비동기로 전송합니다
     * @param toEmail 수신자
     * @param title 메일 제목
     * @param message 내용
     */
    void asyncSend(String toEmail, String title, String message);

    /**
     * 메일을 동기식으로 전송합니다. 이는 응답 레이턴시를 증가시키기 떄문에 추천되지 않습니다.
     * @param toEmail 수신자
     * @param title 메일 제목
     * @param message 내용
     */
    void send(String toEmail, String title ,String message);

}
