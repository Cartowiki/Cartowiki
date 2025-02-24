package com.cartowiki.webapp.authentication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cartowiki.webapp.users.model.User;

/**
 * Tests for JWT tokens generation and checking
 */
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class JwtServiceTests {
    @Autowired
    JwtService service;

    String username = "john";
    String validToken;
    String expiredToken;

    /**
     * Generate two tokens as exemples
     */
    @BeforeAll
    void generateExamples() {
        validToken = service.generateToken(username);
        expiredToken = service.generateToken(username, 0);
    }

    /**
     * Test expiration date retrieval
     */
    @Test
    void testExtractExpiration() {
        assertNotNull(service.extractExpiration(validToken));   
    }

    /**
     * Test username retrieval
     */
    @Test
    void testExtractUsername() {
        assertEquals(username, service.extractUsername(validToken));
    }

    /**
     * Test verifying token validity
     */
    @Test
    void testValidateToken() {
        User user = new User(username, "", "", 0);

        assertTrue(service.validateToken(validToken, user));
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> service.validateToken(expiredToken, user));
    }
}
