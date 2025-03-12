package com.cartowiki.webapp.users.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * User edit request tests
 */
class EditRequestTests {
    /**
     * Test if username is empty
     */
    @Test
    void testIsUsernameEmpty() {
        EditRequest request = new EditRequest();

        assertTrue(request.isUsernameEmpty());
        
        request.setUsername("test");

        assertFalse(request.isUsernameEmpty());
    }

    /**
     * Test if email is empty
     */
    @Test
    void testIsEmailEmpty() {
        EditRequest request = new EditRequest();

        assertTrue(request.isEmailEmpty());
        
        request.setEmail("test");

        assertFalse(request.isEmailEmpty());
    }

    /**
     * Test if password is empty
     */
    @Test
    void testIsPasswordEmpty() {
        EditRequest request = new EditRequest();

        assertTrue(request.isPasswordEmpty());
        
        request.setPassword("test");

        assertFalse(request.isPasswordEmpty());
    }

    /**
     * Test if role is empty
     */
    @Test
    void testIsRoleEmpty() {
        EditRequest request = new EditRequest();

        assertTrue(request.isRoleEmpty());
        
        request.setRole("test");

        assertFalse(request.isRoleEmpty());
    }
}
