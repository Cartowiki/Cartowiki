package com.cartowiki.webapp.util;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * HTTP responses maker
 */
public abstract class ResponseMaker {
    public static final String MESSAGE = "message";
    public static final String ERROR = "error";
    public static final String TOKEN = "token";

    /**
     * Return a single value response for converting to JSON
     * @param key Key
     * @param value Value
     * @param httpStatus Http Status for status code
     * @return Response entity
     */
    public static ResponseEntity<Object> singleValueResponse(String key, String value, HttpStatus httpStatus) {
        HashMap<String, String> map = new HashMap<>();

        map.put(key, value);

        return new ResponseEntity<>(map, httpStatus);
    }
}
