package com.thinkfree.tfinder.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfig {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer postgresql() {
        return new PostgreSQLContainer("postgresql:latest")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
    }

}
