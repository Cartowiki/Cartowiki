package com.cartowiki.webapp.usermanagement.controller;

import javax.naming.SizeLimitExceededException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cartowiki.webapp.usermanagement.model.User;
import com.cartowiki.webapp.usermanagement.model.request.*;
import com.cartowiki.webapp.usermanagement.service.UserService;

/**
 * Authentification requests manager
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    UserService service;

    /**
     * Autowired controller
     * @param service Service for user management
     */
    @Autowired
    public AuthController(UserService service) {
        this.service = service;
    }

    /**
     * Sign up endpoint
     * @param data User's data for sign up
     * @return Response status code and message if error
     */
    @PostMapping("/signup")
    public ResponseStatusException signUp(@RequestBody SignUpRequest data) {
        if (data.getUsername().equals("") || data.getMail().equals("") || data.getPassword().equals("")) {
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some arguments are missing");
        }
        else {
            try {
                // Create a new contributor
                service.addUser(new User(data.getUsername(), data.getMail(), data.getPassword(), 0));
                return new ResponseStatusException(HttpStatus.CREATED);
            }
            catch (UsernameNotFoundException e) {
                return new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }
    }
}
