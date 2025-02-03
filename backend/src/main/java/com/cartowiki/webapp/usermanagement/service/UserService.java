package com.cartowiki.webapp.usermanagement.service;

import java.util.Optional;

import javax.naming.SizeLimitExceededException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cartowiki.webapp.usermanagement.model.User;
import com.cartowiki.webapp.usermanagement.repository.UserRepository;

/**
 * Operations on User instances
 */
@Service
public class UserService {
    @Autowired
    UserRepository repository;

    /**
     * Check for the string fields' size according to the database restrictions :
     *  - mail : 128 char. max.
     *  - username : 32 char. max.
     *  - passwordHash : 128 char. max.
     * @param user User instance to check
     * @return Flag if all the fields' sizes are legit
     */
    public boolean checkFieldsSize(User user) {
        return user.getMail().length() <= 128 && user.getUsername().length() <= 32 && user.getPassword().length() <= 128;
    }

    /**
     * Save a new or existing user in the database
     * @param user User to save
     * @return Instance of the saved user
     * @throws SizeLimitExceededException One field doesn't respect the database's limitations
     */
    public User addUser(User user) throws SizeLimitExceededException {
        BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();
        
        user.setPassword(bCryptEncoder.encode(user.getPassword()));

        if (this.checkFieldsSize(user)) {
            user = repository.save(user);
        }
        else {
            throw new SizeLimitExceededException("One field doesn't respect the database's limitations");
        }

        return user;
    }
}
