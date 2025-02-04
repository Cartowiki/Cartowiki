package com.cartowiki.webapp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
}
