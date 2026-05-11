package com.thinkfree.tfinder.annotation;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 통합 테스트 클래스에 이 어노테이션을 달면 단위 테스트만 빠르게 테스트할 수 있습니다.
 * 구성 편집에서 JUnit5를 추가하고, 태그에 !integration을 설정하세요.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Tag("integration")
public @interface IntegrationTest {
}
