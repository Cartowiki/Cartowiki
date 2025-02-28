package com.cartowiki.webapp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cartowiki.webapp.users.model.User;

/**
 * HTTP responses maker
 */
public abstract class ResponseMaker {
    public static final String MESSAGE = "message";
    public static final String TOKEN = "token";
    public static final String DATA = "data";

    /**
     * No-argument constructor
     */
    private ResponseMaker() {
        // Overwrite public default constructor to private one because ResponseMaker is a utility class
    }

    /**
     * Return a single value response for converting to JSON
     * @param key Key
     * @param value Value
     * @param httpStatus Http Status for status code
     * @return Response entity
     */
    public static ResponseEntity<Object> singleValueResponse(String key, Object value, HttpStatus httpStatus) {
        HashMap<String, Object> map = new HashMap<>();

        map.put(key, value);

        return new ResponseEntity<>(map, httpStatus);
    }

    /**
     * Return a reponse containing user's username, email and role
     * @param user User
     * @return Response entity
     */
    public static ResponseEntity<Object> userInfoResponse(User user) {
        HashMap<String, String> map = new HashMap<>();

        map.put("username", user.getUsername());
        map.put("email", user.getEmail());
        map.put("role", user.getRole());

        return singleValueResponse(DATA, map, HttpStatus.OK);
    }

    /**
     * Return a list of users' public data
     * @param listUsers List of users
     * @return Response entity
     */
    public static ResponseEntity<Object> listUsersInfoResponse(Collection<User> listUsers) {
        ArrayList<HashMap<String, String>> data = new ArrayList<>();

        // For each user, send only the username, email and role
        for (User user: listUsers) {
            HashMap<String, String> map = new HashMap<>();

            map.put("username", user.getUsername());
            map.put("email", user.getEmail());
            map.put("role", user.getRole());

            data.add(map);
        }

        return singleValueResponse(DATA, data, HttpStatus.OK);
    }
}
