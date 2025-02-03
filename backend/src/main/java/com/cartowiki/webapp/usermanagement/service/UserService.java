package com.cartowiki.webapp.usermanagement.service;

import java.util.Optional;
import java.util.regex.Pattern;

import javax.naming.AuthenticationException;
import javax.naming.SizeLimitExceededException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cartowiki.webapp.usermanagement.model.User;
import com.cartowiki.webapp.usermanagement.repository.UserRepository;

/**
 * Operations on User instances
 */
@Service
public class UserService implements UserDetailsService{
    UserRepository repository;

    /**
     * Autowired constructor
     * @param repository Repository for Users
     */
    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Check for the string fields' size according to the database restrictions :
     *  - email : 128 char. max.
     *  - username : 32 char. max.
     *  - passwordHash : 128 char. max.
     * @param user User instance to check
     * @return Flag if all the fields' sizes are legit
     */
    public boolean checkFieldsSize(User user) {
        return user.getEmail().length() <= 128 && user.getUsername().length() <= 32 && user.getPassword().length() <= 128;
    }

    /**
     * Save a new user in the database
     * @param user User to save
     * @return Instance of the saved user
     * @throws SizeLimitExceededException One field doesn't respect the database's limitations
     * @throws AuthenticationException Username is already taken
     */
    public User addUser(User user) throws SizeLimitExceededException, AuthenticationException {
        BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();
        
        user.setPassword(bCryptEncoder.encode(user.getPassword()));

        if (this.isUsernameTaken(user.getUsername())) {
            throw new AuthenticationException("Username is already taken");
        }
        else if (!this.isEmailValid(user.getEmail())) {
            throw new AuthenticationException("Email address is not valid");
        }
        else if (this.isEmailTaken(user.getEmail())) {
            throw new AuthenticationException("Email address is already taken");
        }
        else if (this.checkFieldsSize(user)) {
            user = repository.save(user);
        }
        else {
            throw new SizeLimitExceededException("One argument is too long");
        }

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optUser = repository.findByUsername(username);

        if (optUser.isEmpty()) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }

        return optUser.get();
    }

    /**
     * Check if a username is in the database
     * @param username Username to search for
     * @return Is the username in the database
     */
    public boolean isUsernameTaken(String username) {
        Optional<User> optUser = repository.findByUsername(username);

        return optUser.isPresent();
    }

    /**
     * Check if a email address is in the database
     * @param email Email address to search for
     * @return Is the email address in the database
     */
    public boolean isEmailTaken(String email) {
        Optional<User> optUser = repository.findByEmail(email);

        return optUser.isPresent();
    }

    /**
     * Check if the email address is valid
     * @param email Email address to check
     * @return Is the email address valid
     */
    public boolean isEmailValid(String email) {
        // Regexp pattern from https://owasp.org/www-community/OWASP_Validation_Regex_Repository
        String pattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}";

        return Pattern.compile(pattern).matcher(email).matches();
    }
}
