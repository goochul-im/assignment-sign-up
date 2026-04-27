package com.thinkfree.tfinder.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 생각해봐야 할 것 -> 한꺼번에 1000개의 메일에 초대를 보내면??
 * 나중에 메시지 큐 기반으로 바꿔야하지 않을까?
 */
@Configuration
@EnableAsync
public class AsyncConfig { // 비동기 스레드를 위해 설정

    @Bean
    public Executor taskExecutor() {
        return new VirtualThreadTaskExecutor("mail-");
    }


}
