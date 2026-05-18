package com.thinkfree.tfinder.common.concurrent;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLockSupporter implements ILockSupporter{

    private final RedissonClient redissonClient;

    public <T> T lockSupport(Supplier<T> task, String lock) {
        RLock myLock = redissonClient.getLock(lock);

        log.info("락 획득 시도");

        try {
            if (myLock.tryLock(5,10, TimeUnit.SECONDS)) {
                return task.get();
            } else {
                log.warn("락 획득 실패");
                throw new BusinessException(ErrorCode.LOCK_ACQUIRE_FAILED, "너무 많은 요청이 몰렸을 수 있습니다. 잠시 후 다시 시도해주세요.");
            }
        } catch (InterruptedException e) {
            log.error("락 획득 중 인터럽트 발생");
            throw new BusinessException(ErrorCode.EXTERNAL_ERROR, "동시성 처리 중 에러 발생");
        } finally {
            if (myLock.isLocked() && myLock.isHeldByCurrentThread())
                myLock.unlock();
        }

    }

}
