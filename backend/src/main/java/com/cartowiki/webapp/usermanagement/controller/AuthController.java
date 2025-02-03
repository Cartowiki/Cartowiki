package com.cartowiki.webapp.usermanagement.controller;

import javax.naming.AuthenticationException;
import javax.naming.SizeLimitExceededException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cartowiki.webapp.usermanagement.model.User;
import com.cartowiki.webapp.usermanagement.model.request.LogInRequest;
import com.cartowiki.webapp.usermanagement.model.request.SignUpRequest;
import com.cartowiki.webapp.usermanagement.service.JwtService;
import com.cartowiki.webapp.usermanagement.service.UserService;
import com.cartowiki.webapp.util.ResponseMaker;

/**
 * Authentication requests manager
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private UserService service;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    /**
     * Autowired controller
     * @param service Service for user management
     * @param jwtService Service for JWT Authentication
     * @param authenticationManager Authentication Manager
     */
    @Autowired
    public AuthController(UserService service, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.service = service;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Sign up endpoint
     * @param data User's data for sign up
     * @return Response
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

    /**
     * Log in endpoint : if successful, returns token
     * @param data User's credentials
     * @return Response
     */
    @PostMapping("/login")
    public ResponseEntity<Object> authenticateAndGetToken(@RequestBody LogInRequest data) {
        ResponseEntity<Object> response;
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword())
        );

        if (authentication.isAuthenticated()) {
            response = ResponseMaker.singleValueResponse(ResponseMaker.TOKEN, jwtService.generateToken(data.getUsername()), HttpStatus.CREATED);
        } else {
            response = ResponseMaker.singleValueResponse(ResponseMaker.ERROR, "Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        return response;
    }
}
