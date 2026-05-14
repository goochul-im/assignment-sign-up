package com.thinkfree.tfinder.concurrency;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.thinkfree.tfinder.annotation.IntegrationTest;
import com.thinkfree.tfinder.auth.infrastructure.persistence.adapter.RedisEmailValidateRepository;
import com.thinkfree.tfinder.auth.infrastructure.persistence.adapter.RedisRefreshTokenRepository;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IEmailValidateRepository;
import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IRefreshTokenRepository;
import com.thinkfree.tfinder.auth.service.dto.SignupDto;
import com.thinkfree.tfinder.auth.service.iface.IAuthUseCase;
import com.thinkfree.tfinder.common.concurrent.LockSupporter;
import com.thinkfree.tfinder.common.config.JwtProperties;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.infrastructure.external.iface.IMailSender;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
@IntegrationTest
@Profile("test")
public class SignupConcurrencyTest {

    @Autowired
    private LockSupporter lockSupporter;
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

    private LettuceConnectionFactory connectionFactory;

    @AfterEach
    void cleanUp() {
        if (connectionFactory != null) {
            connectionFactory.destroy();
        }
    }

    @Test
    void 동시에_여러명이_가입해도_한명만_가입에_성공해야_한다() throws InterruptedException {
        //given
        int threadCount = 30;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        String email = "cuncurrent@email.com";
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        emailValidateRepository = new RedisEmailValidateRepository(redisTemplate());
        emailValidateRepository.saveAsValidated(email, Duration.ofDays(1));
        boolean validate = emailValidateRepository.isRequested(email);
        assertThat(validate).isTrue();

        //when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    lockSupporter.lockSupport(() ->
                                    authUseCase.signUp(new SignupDto(
                                            email,
                                            "just name",
                                            "just password"
                                    )),
                            email
                    );

                    successCount.addAndGet(1);
                } catch (BusinessException e) {
                    System.out.println(e.getErrorCode().getErrorCode());
                    failedCount.addAndGet(1);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executor.close();
        //then

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failedCount.get()).isEqualTo(threadCount - 1);
    }

    private StringRedisTemplate redisTemplate() {
        connectionFactory = new LettuceConnectionFactory(redis.getHost(), redis.getMappedPort(6379));
        connectionFactory.afterPropertiesSet();

        StringRedisTemplate redisTemplate = new StringRedisTemplate(connectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}
