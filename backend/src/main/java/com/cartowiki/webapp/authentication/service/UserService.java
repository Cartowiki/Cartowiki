package com.cartowiki.webapp.authentication.service;

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

import com.cartowiki.webapp.authentication.model.User;
import com.cartowiki.webapp.authentication.repository.UserRepository;

/**
 * Operations on User instances
 */
@Service
public class UserService implements UserDetailsService{
    private static final int PASSWORD_MAX_LENGTH = 128;
    private static final int USERNAME_MAX_LENGTH = 32;
    private static final int EMAIL_MAX_LENGTH = 128;
    
    private UserRepository repository;

    /**
     * Autowired constructor
     * @param repository Repository for Users
     */
    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
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

        if (user.getUsername().length() > USERNAME_MAX_LENGTH) {
            throw new SizeLimitExceededException("Username is too long");
        }
        else if (user.getEmail().length() > EMAIL_MAX_LENGTH) {
            throw new SizeLimitExceededException("Email is too long");
        }
        else if (user.getPassword().length() > PASSWORD_MAX_LENGTH) {
            throw new SizeLimitExceededException("Username is too long");
        }
        else if (this.isUsernameTaken(user.getUsername())) {
            throw new AuthenticationException("Username is already taken");
        }
        else if (!this.isEmailValid(user.getEmail())) {
            throw new AuthenticationException("Email address is not valid");
        }
        else if (this.isEmailTaken(user.getEmail())) {
            throw new AuthenticationException("Email address is already taken");
        }
        else {
            user = repository.save(user);
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
        // Inspired from https://owasp.org/www-community/OWASP_Validation_Regex_Repository
        // Edit : limit the number of repetitions in the address
        String pattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+){0,10}@(?:[a-zA-Z0-9-]+\\.){0,10}[a-zA-Z]{2,7}";

        return Pattern.compile(pattern).matcher(email).matches();
    }
}
