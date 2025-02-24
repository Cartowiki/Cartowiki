package com.cartowiki.webapp.users.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    private User administrator;

    /**
     * Add a know user to the database before tests
     */
    @BeforeAll
    void populateDatabase() {
        administrator = repository.save(new User("B72GCQ3aVvY3pqAPKQ7vqrk1D3byhFsM", "B72GCQ3aVvY3pqAPKQ7vqrk1D3byhFsM@domain.net", "P@ssw0rd", 0));
    }

    /**
     * Remove created users during the tests
     */
    @AfterAll
    void unpopulateDatabase() {
        repository.deleteAll();
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
            service.addUser(new User("wqNOM8H4NLUY9QMDOkwMPf9RDgvSd7zT", "wqNOM8H4NLUY9QMDOkwMPf@9RDgvSd7.zT", "pass", 0));
        });
    }

    @Test
    void testLoadUserByUsername() {
        // Existing user
        UserDetails retrievedUser = service.loadUserByUsername(administrator.getUsername());

        assertEquals(administrator.getUsername(), retrievedUser.getUsername());
        assertEquals(administrator.getPassword(), retrievedUser.getPassword());

        // Absent user
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class, () -> service.loadUserByUsername("bG15ie6eMnAbfJrJLwTVUGXMdyJpC4vQ"));
    }
}
