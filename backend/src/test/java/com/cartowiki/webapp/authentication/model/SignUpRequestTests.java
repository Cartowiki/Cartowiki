package com.cartowiki.webapp.authentication.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SignUpRequestTests {
    /**
     * Test if username is empty
     */
    @Test
    void testIsUsernameEmpty() {
        SignUpRequest request = new SignUpRequest();

        assertTrue(request.isUsernameEmpty());
        
        request.setUsername("test");

        assertFalse(request.isUsernameEmpty());
    }

    /**
     * Test if email is empty
     */
    @Test
    void testIsEmailEmpty() {
        SignUpRequest request = new SignUpRequest();

        assertTrue(request.isEmailEmpty());
        
        request.setEmail("test");

        assertFalse(request.isEmailEmpty());
    }

    /**
     * Test if password is empty
     */
    @Test
    void testIsPasswordEmpty() {
        SignUpRequest request = new SignUpRequest();

        assertTrue(request.isPasswordEmpty());
        
        request.setPassword("test");

        assertFalse(request.isPasswordEmpty());
    }
}
