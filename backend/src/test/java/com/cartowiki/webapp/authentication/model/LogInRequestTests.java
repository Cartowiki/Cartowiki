package com.cartowiki.webapp.authentication.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LogInRequestTests {
    /**
     * Test if username is empty
     */
    @Test
    void testIsUsernameEmpty() {
        LogInRequest request = new LogInRequest();

        assertTrue(request.isUsernameEmpty());
        
        request.setUsername("test");

        assertFalse(request.isUsernameEmpty());
    }

    /**
     * Test if password is empty
     */
    @Test
    void testIsPasswordEmpty() {
        LogInRequest request = new LogInRequest();

        assertTrue(request.isPasswordEmpty());
        
        request.setPassword("test");

        assertFalse(request.isPasswordEmpty());
    }
}
