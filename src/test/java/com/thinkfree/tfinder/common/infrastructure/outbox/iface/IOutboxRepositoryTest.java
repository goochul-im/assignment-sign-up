package com.thinkfree.tfinder.common.infrastructure.outbox.iface;

import com.thinkfree.tfinder.annotation.IntegrationTest;
import com.thinkfree.tfinder.common.infrastructure.outbox.OutboxEntity;
import com.thinkfree.tfinder.common.infrastructure.outbox.enumrate.OutboxEventType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@IntegrationTest
class IOutboxRepositoryTest {

    @Autowired
    IOutboxRepository outboxRepository;
    @Autowired
    EntityManagerFactory factory;
    @Autowired
    EntityManager em;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void 락이_잡혀있는_row는_스킵하고_가져온다() throws InterruptedException, ExecutionException, JsonProcessingException {
        //given
//        EntityManager em = factory.createEntityManager();
//        EntityTransaction transaction = em.getTransaction();
//        transaction.begin();
        for (int i = 0; i < 150; i++) {
            outboxRepository.save(new OutboxEntity(
                    OutboxEventType.JOIN_WORKSPACE_PENDING_INVITE,
                    mapper.writeValueAsString("hi")
            ));
        }
        int limit = 10;

        em.flush();
        em.clear();
//        transaction.commit();

        System.out.println(outboxRepository.count());

        //when
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CountDownLatch countDownLatch = new CountDownLatch(3);
        CountDownLatch start = new CountDownLatch(1);

        Callable<List<OutboxEntity>> task = () -> {
            countDownLatch.countDown();
            start.await();
//            EntityManager entityManager = factory.createEntityManager();
//            EntityTransaction tx = entityManager.getTransaction();
//            tx.begin();

            //TODO: 왜 엔티티들을 못가져오지?
            List<OutboxEntity> pendingForUpdate = outboxRepository.findPendingForUpdate(limit);
            System.out.println("thread name = " + Thread.currentThread().getName() + ", count = " + outboxRepository.count());
            System.out.println("list size = " + pendingForUpdate.size());

//            tx.commit();
            return pendingForUpdate;
        };

        Future<List<OutboxEntity>> submit1 = executor.submit(task);
        Future<List<OutboxEntity>> submit2 = executor.submit(task);
        Future<List<OutboxEntity>> submit3 = executor.submit(task);

        countDownLatch.await();
        start.countDown();

        Thread.sleep(2000);

        List<Long> list1 = submit1.get().stream().map(OutboxEntity::getId).toList();
        List<Long> list2 = submit2.get().stream().map(OutboxEntity::getId).toList();
        List<Long> list3 = submit3.get().stream().map(OutboxEntity::getId).toList();
        System.out.println(outboxRepository.count());

        //then
        System.out.println("list1 : " + list1);
        System.out.println("list2 : " + list2);
        System.out.println("list3 : " + list3);
        assertNotSame(list1, list2);
        assertNotSame(list2, list3);
        assertNotSame(list1, list3);
        executor.close();
    }

}
