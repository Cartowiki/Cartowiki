package com.cartowiki.webapp.authentication.model.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LogInRequestTests {
    /**
     * Test default constructor
     */
    @Test
    void testDefaultConstructor() {
        LogInRequest request = new LogInRequest();

        assertEquals("", request.getUsername());
        assertEquals("", request.getPassword());
    }
}
