package com.cartowiki.webapp.authentication.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SignUpRequestTests {
    /**
     * Test default constructor
     */
    @Test
    void testDefaultConstructor() {
        SignUpRequest request = new SignUpRequest();

        assertEquals("", request.getUsername());
        assertEquals("", request.getEmail());
        assertEquals("", request.getPassword());
    }
}
