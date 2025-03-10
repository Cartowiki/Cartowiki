package com.cartowiki.webapp.users.service;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.naming.AuthenticationException;
import javax.naming.SizeLimitExceededException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cartowiki.webapp.users.config.DatabaseConfig;
import com.cartowiki.webapp.users.model.User;
import com.cartowiki.webapp.users.repository.UserRepository;

/**
 * Operations on User instances
 */
@Service
public class UserService implements UserDetailsService{    
    private DatabaseConfig databaseConfig;
    private UserRepository repository;

    /**
     * Autowired constructor
     * @param repository Repository for Users
     * @param databaseConfig Database restrictions
     */
    @Autowired
    public UserService(UserRepository repository, DatabaseConfig databaseConfig) {
        this.repository = repository;
        this.databaseConfig = databaseConfig;
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

        if (user.getUsername().length() > databaseConfig.getUsernameMaxLength()) {
            throw new SizeLimitExceededException("Username is too long");
        }
        else if (user.getEmail().length() > databaseConfig.getEmailMaxLength()) {
            throw new SizeLimitExceededException("Email is too long");
        }
        // Password size is not checked because bCrypt generate 60-character hash
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
    private boolean isUsernameTaken(String username) {
        Optional<User> optUser = repository.findByUsername(username);

        return optUser.isPresent();
    }

    /**
     * Check if a email address is in the database
     * @param email Email address to search for
     * @return Is the email address in the database
     */
    private boolean isEmailTaken(String email) {
        Optional<User> optUser = repository.findByEmail(email);

        return optUser.isPresent();
    }

    /**
     * Check if the email address is valid
     * @param email Email address to check
     * @return Is the email address valid
     */
    private boolean isEmailValid(String email) {
        // Inspired from https://owasp.org/www-community/OWASP_Validation_Regex_Repository
        // Edit : limit the number of repetitions in the address
        String pattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+){0,10}@(?:[a-zA-Z0-9-]+\\.){0,10}[a-zA-Z]{2,7}";

        return Pattern.compile(pattern).matcher(email).matches();
    }

    /**
     * Return a User from the database if it has less priviledges
     * @param id Target user's id
     * @param requester User who makes the request
     * @return User
     * @throws MissingResourceException User searched not found
     * @throws AuthorizationDeniedException User has more priviledges than the reference
     */
    public User getUser(int id, User requester) throws MissingResourceException, AuthorizationDeniedException {
        Optional<User> optUser = repository.findById(id);

        if (optUser.isEmpty()) {
            throw new MissingResourceException("Missing user", "User", String.valueOf(id));
        }
        else if (requester.hasEqualOrHigherPriviledgesThan(optUser.get())) {
            return optUser.get();
        }
        else {
            throw new AuthorizationDeniedException("Missing priviledge");
        }
    }

    /**
     * Return the list of all users with less or equal priviledges
     * @param requester User who makes the request
     * @return List of users with less or equal priviledges
     */
    public List<User> getAllUsers(User requester) {
        ArrayList<Integer> adminLevels = new ArrayList<>();

        switch (requester.getAdminLevel()) {
            case User.CONTRIBUTOR_CODE:
                adminLevels.add(User.CONTRIBUTOR_CODE);
                break;

            case User.ADMINISTRATOR_CODE:
                adminLevels.add(User.CONTRIBUTOR_CODE);
                adminLevels.add(User.ADMINISTRATOR_CODE);
                break;

            case User.SUPERADMINISTRATOR_CODE:
                adminLevels.add(User.CONTRIBUTOR_CODE);
                adminLevels.add(User.ADMINISTRATOR_CODE);
                adminLevels.add(User.SUPERADMINISTRATOR_CODE);
                break;
            
            default:
                // List already empty
                break;
        }

        return repository.findAllByAdminLevelIn(adminLevels);
    }

    /**
     * Delete a user if it has less or equal privileges
     * @param id Target user's id
     * @param requester User who makes the request
     * @throws MissingResourceException User not found or already deleted
     * @throws AuthorizationDeniedException Requester doesn't have enough priviledge
     */
    public void deleteUser(int id, User requester) throws MissingResourceException, AuthorizationDeniedException{
        Optional<User> user = repository.findById(id);

        if (user.isEmpty()) {
            throw new MissingResourceException("Missing user", "User", String.valueOf(id));
        }
        else if (!user.get().isEnabled()) {
            throw new MissingResourceException("User already deleted", "User", String.valueOf(id));
        }
        else if (requester.hasEqualOrHigherPriviledgesThan(user.get())) {
            // Soft-delete a user
            user.get().setEnabled(false);

            repository.save(user.get());
        }
        else {
            throw new AuthorizationDeniedException("Missing priviledge");
        }
    }
    
    /**
     * Change the username of a user
     * @param user Target user
     * @param username New username
     * @return User with changed username
     * @throws IllegalArgumentException Username is already taken
     */
    public User changeUsername(User user, String username) throws IllegalArgumentException {
        if (this.isUsernameTaken(username)) {
            throw new IllegalArgumentException("Username is already taken");
        }
        else {
            user.setUsername(username);
        }

        return user;
    }

    /**
     * Change the email address of a user
     * @param user Target user
     * @param email New email address
     * @return User with changed email address
     * @throws IllegalArgumentException Email address is already taken or isn't valid
     */
    public User changeEmail(User user, String email) throws IllegalArgumentException {
        if (!this.isEmailValid(email)) {
            throw new IllegalArgumentException("Email is invalid");
        }   
        else if (this.isEmailTaken(email)) {
            throw new IllegalArgumentException("Email is already taken");
        }
        else {
            user.setEmail(email);
        }

        return user;
    }

    /**
     * Change the password of a user
     * @param user Target user
     * @param password New password
     * @return User with changed password
     */
    public User changePassword(User user, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        user.setPassword(encoder.encode(password));

        return user;
    }

    /**
     * Change the role of a user
     * @param user Target user
     * @param role New role
     * @param requester Requester
     * @return User with changed role
     * @throws AuthorizationDeniedException Illegal role change
     * @throws IllegalArgumentException Unknown role
     */
    public User changeRole(User user, String role, User requester) throws AuthorizationDeniedException, IllegalArgumentException{
        if (!role.equals(User.CONTRIBUTOR) && !role.equals(User.ADMINISTRATOR) && !role.equals(User.SUPERADMINISTRATOR)) {
            throw new IllegalArgumentException("Unknown role");
        }
        else if ((requester.getAdminLevel() == User.CONTRIBUTOR_CODE) || (requester.getAdminLevel() == User.ADMINISTRATOR_CODE && role.equals(User.SUPERADMINISTRATOR))) {
            // A contributor cannot change role
            // A administrator cannot change role to superadmin

            throw new AuthorizationDeniedException("Illegal role change");
        }
        else {
            switch (role) {
                case User.CONTRIBUTOR:
                    user.setAdminLevel(User.CONTRIBUTOR_CODE);
                    break;

                case User.ADMINISTRATOR:
                    user.setAdminLevel(User.ADMINISTRATOR_CODE);
                    break;

                case User.SUPERADMINISTRATOR:
                    user.setAdminLevel(User.SUPERADMINISTRATOR_CODE);
                    break;

                default:
                    // Do nothing
                    break;
            }
        }
        
        return user;
    }

    /**
     * Save a user instance
     * @param user Instance to save
     */
    public void saveUser(User user) {
        repository.save(user);
    }
}
