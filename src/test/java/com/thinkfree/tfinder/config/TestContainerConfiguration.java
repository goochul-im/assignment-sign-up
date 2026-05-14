package com.thinkfree.tfinder.config;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;

@Configuration
public class TestContainerConfiguration {

    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:16-alpine");

    @PreDestroy
    void preDestroy() {

    }

    static {
        POSTGRESQL_CONTAINER.start();
    }

}
