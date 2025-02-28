package com.cartowiki.webapp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cartowiki.webapp.users.model.User;

/**
 * Tests of ResponseMaker
 */
class ResponseMakerTests {
    /**
     * Test content of a ResponseEntity built by ReponseMaker
     */
    @Test
    void testSingleValueResponse() {
        String key = "key";
        String value = "value";
        HttpStatus status = HttpStatus.OK;

        ResponseEntity<Object> response = ResponseMaker.singleValueResponse(key, value, status);
        HashMap<String, String> map = (HashMap<String, String>) response.getBody();

        assertEquals(status, response.getStatusCode());
        assertEquals(1, map.size()); // One Key/Value
        assertEquals(value, map.get(key));
    }

    /**
     * Test the response containing the User's public data
     */
    @Test
    void testUserInfoResponse() {
        User user = new User("john", "john.doe@email.com", "P@ssw0rd", 1);

        ResponseEntity<Object> response = ResponseMaker.userInfoResponse(user);
        HashMap<String, Object> map = (HashMap<String, Object>) response.getBody();
        HashMap<String, Object> data = (HashMap<String, Object>) map.get(ResponseMaker.DATA);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getId(), data.get("id"));
        assertEquals(user.getUsername(), data.get("username"));
        assertEquals(user.getEmail(), data.get("email"));
        assertEquals(user.getRole(), data.get("role"));
    }
}
