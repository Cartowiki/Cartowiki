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

        assertTrue(request.checkForEmptyUsername());
        
        request.setUsername("test");

        assertFalse(request.checkForEmptyUsername());
    }

    /**
     * Test if email is empty
     */
    @Test
    void testIsEmailEmpty() {
        EditRequest request = new EditRequest();

        assertTrue(request.checkForEmptyEmail());
        
        request.setEmail("test");

        assertFalse(request.checkForEmptyEmail());
    }

    /**
     * Test if password is empty
     */
    @Test
    void testIsPasswordEmpty() {
        EditRequest request = new EditRequest();

        assertTrue(request.checkForEmptyPassword());
        
        request.setPassword("test");

        assertFalse(request.checkForEmptyPassword());
    }

    /**
     * Test if role is empty
     */
    @Test
    void testIsRoleEmpty() {
        EditRequest request = new EditRequest();

        assertTrue(request.checkForEmptyRole());
        
        request.setRole("test");

        assertFalse(request.checkForEmptyRole());
    }
}
