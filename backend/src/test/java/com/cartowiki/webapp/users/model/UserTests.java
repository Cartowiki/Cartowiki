package com.cartowiki.webapp.users.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;


/**
 * Tests for User
 */
@SpringBootTest
class UserTests {
    // Mock data
    private String username = "John";
    private String email = "john@domain.net";
    private String password = "P@ssw0rd!";

    // Mock users
    private User contributor = new User(username, email, password, 0);
    private User admin = new User(username, email, password, 1);
    private User superadmin = new User(username, email, password, 2);
    private User unknown = new User(username, email, password, 999);

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
     * Test role's name depending on adminLevel
     */
    @Test
    void testGetRole() {
        // Contributor
        assertEquals(User.CONTRIBUTOR, contributor.getRole());

        // Administrator
        assertEquals(User.ADMINISTRATOR, admin.getRole());

        // Superadministrator
        assertEquals(User.SUPERADMINISTRATOR, superadmin.getRole());

        // Unknown
        assertEquals(User.UNKNOWN, unknown.getRole());
    }

    /**
     * Test authorities depending on adminLevel
     */
    @Test
    void testGetAuthorities() {
        // Test contributor
        assertEquals("ROLE_" + User.CONTRIBUTOR, ((GrantedAuthority) contributor.getAuthorities().toArray()[0]).getAuthority());

        // Test admin
        assertEquals("ROLE_" + User.ADMINISTRATOR, ((GrantedAuthority) admin.getAuthorities().toArray()[0]).getAuthority());

        // Test superadmin (not implemented yet)
        assertEquals("ROLE_" + User.SUPERADMINISTRATOR, ((GrantedAuthority) superadmin.getAuthorities().toArray()[0]).getAuthority());

        // Test other
        assertTrue(unknown.getAuthorities().isEmpty());
    }

    /**
     * Test the test of privileges beetween users
     */
    @Test
    void testHasEqualOrHigherPriviledgesThan() {        
        // Contributor
        assertTrue(contributor.hasEqualOrHigherPriviledgesThan(contributor));
        assertFalse(contributor.hasEqualOrHigherPriviledgesThan(admin));
        assertFalse(contributor.hasEqualOrHigherPriviledgesThan(superadmin));
        assertFalse(contributor.hasEqualOrHigherPriviledgesThan(unknown));

        // Administrator
        assertTrue(admin.hasEqualOrHigherPriviledgesThan(contributor));
        assertTrue(admin.hasEqualOrHigherPriviledgesThan(admin));
        assertFalse(admin.hasEqualOrHigherPriviledgesThan(superadmin));
        assertTrue(admin.hasEqualOrHigherPriviledgesThan(unknown));

        // Superadministrator
        assertTrue(superadmin.hasEqualOrHigherPriviledgesThan(contributor));
        assertTrue(superadmin.hasEqualOrHigherPriviledgesThan(admin));
        assertTrue(superadmin.hasEqualOrHigherPriviledgesThan(superadmin));
        assertTrue(superadmin.hasEqualOrHigherPriviledgesThan(unknown));

        // Unknown
        assertFalse(unknown.hasEqualOrHigherPriviledgesThan(contributor));
        assertFalse(unknown.hasEqualOrHigherPriviledgesThan(admin));
        assertFalse(unknown.hasEqualOrHigherPriviledgesThan(superadmin));
        assertFalse(unknown.hasEqualOrHigherPriviledgesThan(unknown));
    }
}
