package com.thinkfree.tfinder;

import com.thinkfree.tfinder.annotation.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@IntegrationTest
class SignUpApplicationTests {

    @Test
    void contextLoads() {
    }

}
