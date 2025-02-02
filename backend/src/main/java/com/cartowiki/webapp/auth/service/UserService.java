package com.cartowiki.webapp.auth.service;

import org.springframework.stereotype.Service;

import com.cartowiki.webapp.auth.model.User;

/**
 * Operations on User instances
 */
@Service
public class UserService {
    /**
     * Check for the string fields' size according to the database restrictions :
     *  - mail : 128 char. max.
     *  - pseudo : 32 char. max.
     *  - passwordHash : 128 char. max.
     * @param user User instance to check
     * @return Flag if all the fields' sizes are legit
     */
    public boolean checkFieldsSize(User user) {
        return user.getMail().length() <= 128 && user.getPseudo().length() <= 32 && user.getPasswordHash().length() <= 128;
    }
}
