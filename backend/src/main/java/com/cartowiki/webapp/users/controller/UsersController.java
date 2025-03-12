package com.cartowiki.webapp.users.controller;

import java.util.List;
import java.util.MissingResourceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cartowiki.webapp.users.model.EditRequest;
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
     * Return a response containing the user's public data
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

    /**
     * Delete a user
     * @param id User's id
     * @param authentication Current user's authentication
     * @return Response 
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") int id, Authentication authentication) {
        ResponseEntity<Object> response;
        
        try {
            service.deleteUser(id, (User) authentication.getPrincipal());
            response = ResponseMaker.singleValueResponse(ResponseMaker.MESSAGE, "User successfully deleted", HttpStatus.OK);
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
     * Edit a user
     * @param id Id of the user to edit
     * @param data New values for user
     * @param authentication Current user's authentication
     * @return Response
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> editUser(@PathVariable("id") int id, @RequestBody EditRequest data, Authentication authentication) {
        ResponseEntity<Object> response;
        User requester = (User) authentication.getPrincipal();

        try {
            // Throws an exception if not found, or illegal access
            User user = service.getUser(id, requester);

            if (!data.checkForEmptyUsername()) {
                user = service.changeUsername(user, data.getUsername());
            }

            if (!data.checkForEmptyEmail()) {
                user = service.changeEmail(user, data.getEmail());
            }

            if (!data.checkForEmptyPassword()) {
                user = service.changePassword(user, data.getPassword());
            }

            if (!data.checkForEmptyRole()) {
                user = service.changeRole(user, data.getRole(), requester);
            }

            service.saveUser(user);
            response = ResponseMaker.singleValueResponse(ResponseMaker.MESSAGE, "User successfully edited", HttpStatus.ACCEPTED);
        }
        catch (MissingResourceException e) {
            response = ResponseMaker.singleValueResponse(ResponseMaker.MESSAGE, e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (IllegalArgumentException e) {
            response = ResponseMaker.singleValueResponse(ResponseMaker.MESSAGE, e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (AuthorizationDeniedException e) {
            response = ResponseMaker.singleValueResponse(ResponseMaker.MESSAGE, e.getMessage(), HttpStatus.FORBIDDEN);
        }
        
        return response;
    }
}
