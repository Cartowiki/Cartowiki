package com.cartowiki.webapp.usermanagement.controller;

import javax.naming.AuthenticationException;
import javax.naming.SizeLimitExceededException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cartowiki.webapp.usermanagement.model.User;
import com.cartowiki.webapp.usermanagement.model.request.SignUpRequest;
import com.cartowiki.webapp.usermanagement.service.UserService;
import com.cartowiki.webapp.util.ResponseMaker;

/**
 * Authentication requests manager
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private UserService service;

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
     */
    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(@RequestBody SignUpRequest data){
        ResponseEntity<Object> response;

        if (data.getUsername().equals("") || data.getMail().equals("") || data.getPassword().equals("")) {
            response = ResponseMaker.singleValueResponse(ResponseMaker.ERROR, "Some arguments are missing", HttpStatus.BAD_REQUEST);
        }
        else {
            try {
                // Create a new contributor
                service.addUser(new User(data.getUsername(), data.getMail(), data.getPassword(), 0));
                response = ResponseMaker.singleValueResponse(ResponseMaker.MESSAGE, "New contributor created", HttpStatus.CREATED);
            }
            catch (SizeLimitExceededException e) {
                response = ResponseMaker.singleValueResponse(ResponseMaker.ERROR, e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            catch (AuthenticationException e) {
                response = ResponseMaker.singleValueResponse(ResponseMaker.ERROR, e.getMessage(), HttpStatus.CONFLICT);
            }
        }

        return response;
    }
}
