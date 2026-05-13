package com.thinkfree.tfinder.common.aop;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.SourceLocation;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class RedisExceptionAspect {

    @AfterThrowing(value = "execution(* com.thinkfree.tfinder.auth.infrastructure.persistence.adapter.Redis*.*(..))", throwing = "e")
    public void afterConnectionFailed(JoinPoint joinPoint, RedisConnectionFailureException e) {
        log.error("Redis와의 연결이 끊어졌습니다.");
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        log.info("methodName = {}",methodName);
        log.info("error location = {}", signature.toLongString());
        throw new BusinessException(ErrorCode.EXTERNAL_ERROR); //TODO: AOP를 굳이 해야하는가? 이거 그냥 글로벌 예외 처리로 해버려도 될듯한데?
    }

}
