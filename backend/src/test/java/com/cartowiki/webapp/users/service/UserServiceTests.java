package com.cartowiki.webapp.users.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import com.cartowiki.webapp.users.config.DatabaseConfig;
import com.cartowiki.webapp.users.model.User;
import com.cartowiki.webapp.users.repository.UserRepository;


@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class UserServiceTests {
    @Autowired
    UserService service;

    @Autowired
    UserRepository repository;

    @Autowired
    DatabaseConfig config;
    
    private String userName = "wqNOM8H4NLUY9QMDOkwMPf9RDgvSd7zT";

    private User administrator;
    private User superadministrator;

    /**
     * Add a know user to the database before tests
     */
    @BeforeAll
    void populateDatabase() {
        administrator = repository.save(new User("B72GCQ3aVvY3pqAPKQ7vqrk1D3byhFsM", "B72GCQ3aVvY3pqAPKQ7vqrk1D3byhFsM@domain.net", "P@ssw0rd", 1));
        superadministrator = repository.save(new User("Y3pqAPKQ7vqrk1D3byhFsM", "Y3pqAPKQ7vqrk1D3byhFsM@domain.net", "P@ssw0rd", 2));
    }

    /**
     * Remove created users during the tests
     */
    @AfterAll
    void unpopulateDatabase() {
        repository.delete(administrator);
        repository.delete(superadministrator);

        // Try to delete user (if it was created)
        Optional<User> user = repository.findByUsername(userName);

        if (user.isPresent()) {
            repository.delete(user.get());
        }
    }

    /**
     * Test the process of adding a user and all verfications
     */
    @Test
    void testAddUser() {
        // Size of inputs
        assertThrows(javax.naming.SizeLimitExceededException.class, () -> service.addUser(new User("a".repeat(config.getUsernameMaxLength() + 1), "", "", 0)));
        assertThrows(javax.naming.SizeLimitExceededException.class, () -> service.addUser(new User("", "a".repeat(config.getEmailMaxLength() + 1), "", 0)));

        // Taken username
        assertThrows(javax.naming.AuthenticationException.class, () -> service.addUser(new User(administrator.getUsername(), "", "", 0)));

        // Invalid email
        assertThrows(javax.naming.AuthenticationException.class, () -> service.addUser(new User("", "abc.example.com", "", 0)));
        assertThrows(javax.naming.AuthenticationException.class, () -> service.addUser(new User("", "a@b@c@example.com", "", 0)));

        // Taken email
        assertThrows(javax.naming.AuthenticationException.class, () -> service.addUser(new User("", administrator.getEmail(), "", 0)));

        // Valid signup
        assertAll(() -> {
            service.addUser(new User(userName, "wqNOM8H4NLUY9QMDOkwMPf@9RDgvSd7.zT", "pass", 0));
        });
    }

    /**
     * Test getting a user by its username
     */
    @Test
    void testLoadUserByUsername() {
        // Existing user
        UserDetails retrievedUser = service.loadUserByUsername(administrator.getUsername());

        assertEquals(administrator.getUsername(), retrievedUser.getUsername());
        assertEquals(administrator.getPassword(), retrievedUser.getPassword());

        // Absent user
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class, () -> service.loadUserByUsername("bG15ie6eMnAbfJrJLwTVUGXMdyJpC4vQ"));
    }

    /**
     * Test getting a user depending on requester's priviledges
     */
    @Test
    void testGetUser() {
        // Non-existing user
        assertThrows(java.util.MissingResourceException.class, () -> service.getUser(-1, administrator));
        
        // Missing priviledge
        assertThrows(org.springframework.security.authorization.AuthorizationDeniedException.class, () -> service.getUser(superadministrator.getId(), administrator));

        // Success
        User result = service.getUser(administrator.getId(), superadministrator);

        assertEquals(administrator.getId(), result.getId());
        assertEquals(administrator.getUsername(), result.getUsername());
        assertEquals(administrator.getEmail(), result.getEmail());
        assertEquals(administrator.getRole(), result.getRole());
    }
}
