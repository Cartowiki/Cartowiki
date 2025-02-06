package com.cartowiki.webapp.authentication.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;

/**
 * Tests for User
 */
@SpringBootTest
class UserTests {
    private String username = "John";
    private String email = "john@domain.net";
    private String password = "P@ssw0rd!";

    /**
     * Test default constructor
     */
    @Test
    void testDefaultConstructor() {
        User user = new User(username, email, password, 0);

        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
    }

    /**
     * Test authorities depending on adminLevel
     */
    @Test
    void testGetAuthorities() {
        User contributor = new User(username, email, password, 0);
        User admin = new User(username, email, password, 1);
        User superadmin = new User(username, email, password, 2);
        User other = new User(username, email, password, 3);

        // Test contributor
        assertEquals("ROLE_CONTRIBUTOR", ((GrantedAuthority) contributor.getAuthorities().toArray()[0]).getAuthority());

        // Test admin
        assertEquals("ROLE_ADMINISTRATOR", ((GrantedAuthority) admin.getAuthorities().toArray()[0]).getAuthority());

        // Test superadmin (not implemented yet)
        assertTrue(superadmin.getAuthorities().isEmpty());

        // Test other
        assertTrue(other.getAuthorities().isEmpty());
    }
}
