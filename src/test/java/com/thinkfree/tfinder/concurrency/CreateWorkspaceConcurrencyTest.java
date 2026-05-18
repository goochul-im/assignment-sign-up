package com.thinkfree.tfinder.concurrency;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.thinkfree.tfinder.annotation.IntegrationTest;
import com.thinkfree.tfinder.common.concurrent.RedisLockSupporter;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.IMemberRepository;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.MemberEntity;
import com.thinkfree.tfinder.workspace.service.adapter.WorkspaceService;
import com.thinkfree.tfinder.workspace.service.dto.CreateWorkspaceCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@SpringBootTest
@Profile("test")
@Testcontainers
public class CreateWorkspaceConcurrencyTest {

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);
    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private IMemberRepository memberRepository;
    @Autowired
    private RedisLockSupporter lockSupporter;

    private final FixtureMonkey fixture = FixtureMonkey.builder()
            .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
            .build();

    @Test
    void 같은_URL을_가진_워크스페이스를_동시에_생성하면_하나만_생성된다() throws InterruptedException {
        //given
        int threadCount = 30;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        String url = "cuncurrentUrl";
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        List<MemberEntity> members = memberRepository.saveAll(IntStream.range(0, threadCount)
                .mapToObj(i -> new MemberEntity(
                        "testmember",
                        "workspace-concurrency-" + i + "@email.com",
                        "testpassword"
                ))
                .toList());

        //when
        for (int i = 0; i < threadCount; i++) {
            int memberId = i;
            executor.submit(() -> {
                try {
                    countDownLatch.countDown();
                    startLatch.await();
                    lockSupporter.lockSupport(() ->
                            workspaceService.create(new CreateWorkspaceCommand(
                                    members.get(memberId).getId(),
                                    "any" + memberId,
                                    url
                            )),
                            url
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
