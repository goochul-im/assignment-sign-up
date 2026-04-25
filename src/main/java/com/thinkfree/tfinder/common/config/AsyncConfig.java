package com.thinkfree.tfinder.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 생각해봐야 할 것 -> 한꺼번에 1000개의 메일에 초대를 보내면??
 */
@Configuration
@EnableAsync
public class AsyncConfig { // 비동기 스레드를 위해 설정

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 최소한으로 유지하는 스레드 수
        executor.setMaxPoolSize(30); // 스레드 풀 최대 갯수
        executor.setQueueCapacity(1000); // 작업 큐 용량 수
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 작업 큐가 꽉 찼거나 큐를 사용할수 없어서 거부될 때 정책
        executor.initialize();
                                                                                         // 호출한 스레드에서 거부된 task를 대신 실행함
        return executor;
    }


}
