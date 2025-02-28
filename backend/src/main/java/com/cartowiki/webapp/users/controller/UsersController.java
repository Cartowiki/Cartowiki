package com.cartowiki.webapp.users.controller;

import java.util.List;
import java.util.MissingResourceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cartowiki.webapp.users.model.User;
import com.cartowiki.webapp.users.service.UserService;
import com.cartowiki.webapp.util.ResponseMaker;

/**
 * Users manager
 */
@RestController
@RequestMapping("/users")
public class UsersController {
    private UserService service;

    /**
     * Autowired constructor
     * @param service Service for user management
     */
    @Autowired
    public UsersController(UserService service) {
        this.service = service;
    }

    /**
     * Return a response with JSON data
     * @param id User's id
     * @param authentication Current user's authentication
     * @return Response 
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") int id, Authentication authentication) {
        ResponseEntity<Object> response;
        
        try {
            User user = service.getUser(id, (User) authentication.getPrincipal());
            response = ResponseMaker.userInfoResponse(user);
        }
        catch (MissingResourceException e) {
            response = ResponseMaker.singleValueResponse(ResponseMaker.MESSAGE, e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (AuthorizationDeniedException e) {
            response = ResponseMaker.singleValueResponse(ResponseMaker.MESSAGE, e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        return response;
    }

    /**
     * Return a response containing the list of all users with less or equal priviledge
     * @param authentication Current user's authentication
     * @return Response
     */
    @GetMapping("")
    public ResponseEntity<Object> getAllLessPriviledgedUsers(Authentication authentication) {
        List<User> data = service.getAllUsers((User) authentication.getPrincipal());

        return ResponseMaker.listUsersInfoResponse(data);
    }
}
