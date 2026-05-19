package com.thinkfree.tfinder.concurrency;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.thinkfree.tfinder.annotation.IntegrationTest;
import com.thinkfree.tfinder.auth.controller.request.SignupRequest;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IEmailValidateRepository;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IRefreshTokenRepository;
import com.thinkfree.tfinder.auth.service.iface.IAuthUseCase;
import com.thinkfree.tfinder.common.util.concurrent.RedisLockSupporter;
import com.thinkfree.tfinder.common.config.JwtProperties;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.infrastructure.external.iface.IMailSender;
import com.thinkfree.tfinder.common.util.jwt.iface.IJwtManager;
import com.thinkfree.tfinder.workspace.domain.IMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@SpringBootTest
@IntegrationTest
@Profile("test")
public class SignupConcurrencyTest {

    @Autowired
    private RedisLockSupporter lockSupporter;
    @Autowired
    private IAuthUseCase authUseCase;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private IMemberRepository memberRepository;
    @Autowired
    private IJwtManager jwtManager;
    @Autowired
    private IRefreshTokenRepository refreshTokenRepository;
    @Autowired
    private IEmailValidateRepository emailValidateRepository;
    @Autowired
    private IMailSender mailSender;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private JwtProperties jwtProperties;

    private final FixtureMonkey fixture = FixtureMonkey.builder()
            .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
            .build();

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Test
    void 동시에_여러명이_가입해도_한명만_가입에_성공해야_한다() throws InterruptedException {
        //given
        int threadCount = 30;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        String email = "cuncurrent@email.com";
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        emailValidateRepository.saveAsValidated(email, Duration.ofDays(1));
        boolean validate = emailValidateRepository.isValidated(email);
        assertThat(validate).isTrue();

        //when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    countDownLatch.countDown();
                    startLatch.await();
                    lockSupporter.lockSupport(() ->
                                    authUseCase.signUp(new SignupRequest(
                                            "just name",
                                            email,
                                            "just password"
                                    )),
                            email
                    );

                    successCount.addAndGet(1);
                } catch (BusinessException e) {
                    failedCount.addAndGet(1);
                } catch (InterruptedException e) {
                    System.out.println("인터럽트 예외");
                }
            });
        }

        countDownLatch.await();
        startLatch.countDown();
        executor.close();
        //then

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failedCount.get()).isEqualTo(threadCount - 1);
    }

}
