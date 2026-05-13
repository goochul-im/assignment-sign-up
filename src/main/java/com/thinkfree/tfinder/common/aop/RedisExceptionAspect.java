package com.thinkfree.tfinder.common.aop;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class RedisExceptionAspect {

    @AfterThrowing(value = "execution(* com.thinkfree.tfinder.auth.infrastructure.persistence.adapter.Redis*.*(..))", throwing = "e")
    public void afterConnectionFailed(JoinPoint joinPoint, RedisConnectionFailureException e) {
        log.error("Redis와의 연결이 끊어졌습니다.");
        throw new BusinessException(ErrorCode.EXTERNAL_ERROR);
    }

}
