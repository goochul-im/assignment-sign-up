package com.thinkfree.tfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
//@EnableAspectJAutoProxy
//@EnableResilientMethods
public class SignUpApplication {

    public static void main(String[] args) {
        SpringApplication.run(SignUpApplication.class, args);
    }

}
