package com.thinkfree.tfinder.workspace.infrastructure.external.adpater;

import com.thinkfree.tfinder.workspace.infrastructure.external.iface.IMailSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
class GoogleMailSenderTest {

    @Autowired
    private IMailSender mailSender;

    @Autowired
    private JavaMailSender javaMailSender;

    @Test
    void 메일_발송이_정상적으로_완료되어야_한다(){
        //given
        StringBuilder sb = new StringBuilder();
        sb.append("안녕하세요 테스트 메일입니다");
        String toEmail = "gooch123@naver.com";
        String title = "테스트 메일";

        //when
        mailSender.send(toEmail, title, sb.toString());

    }

}
