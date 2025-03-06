package com.cartowiki.webapp.users.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    private User contributor;
    private User administrator;
    private User superadministrator;

    /**
     * Add a know user to the database before tests
     */
    @BeforeAll
    void populateDatabase() {
        contributor = repository.save(new User("MgKE4fhZWr26Y9j5X3L7sn", "MgKE4fhZWr26Y@9j5X3L7.sn", "P@ssw0rd", 0, false));
        administrator = repository.save(new User("B72GCQ3aVvY3pqAPKQ7vqrk1D3byhFsM", "B72GCQ3aVvY3pqAPKQ7vqrk1D3byhFsM@domain.net", "P@ssw0rd", 1));
        superadministrator = repository.save(new User("Y3pqAPKQ7vqrk1D3byhFsM", "Y3pqAPKQ7vqrk1D3byhFsM@domain.net", "P@ssw0rd", 2));
    }

    /**
     * Remove created users during the tests
     */
    @AfterAll
    void unpopulateDatabase() {
        repository.delete(contributor);
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

    /**
     * Test getting all users with less or equals priviledges than the requester
     */
    @Test
    void testGetAllUsers() {
        // From contributor
        for (User dbUser: service.getAllUsers(contributor)) {
            assertEquals(User.CONTRIBUTOR_CODE, dbUser.getAdminLevel());
        }

        // From administrator
        for (User dbUser: service.getAllUsers(administrator)) {
            assertTrue(dbUser.getAdminLevel() == User.CONTRIBUTOR_CODE || dbUser.getAdminLevel() == User.ADMINISTRATOR_CODE);
        }

        // From superadministrator
        for (User dbUser: service.getAllUsers(superadministrator)) {
            assertTrue(dbUser.getAdminLevel() == User.CONTRIBUTOR_CODE || dbUser.getAdminLevel() == User.ADMINISTRATOR_CODE || dbUser.getAdminLevel() == User.SUPERADMINISTRATOR_CODE);
        }
    }

    /**
     * Test the soft-deletion of a user
     */
    @Test
    void testDeleteUser() {
        // Non-existing user
        assertThrows(java.util.MissingResourceException.class, () -> service.deleteUser(-1, administrator));

        // Deleted user
        assertThrows(java.util.MissingResourceException.class, () -> service.deleteUser(contributor.getId(), administrator));
        
        // Missing priviledge
        assertThrows(org.springframework.security.authorization.AuthorizationDeniedException.class, () -> service.getUser(superadministrator.getId(), administrator));
        
        // Success
        assertAll(() -> {
            service.deleteUser(administrator.getId(), superadministrator);
            administrator = repository.findById(administrator.getId()).get();
        });

        assertFalse(administrator.isEnabled());
    }

    /**
     * Test changing username
     */
    @Test
    void testChangeUsername() {
        // Already used username
        String usedName = administrator.getUsername();

        assertThrows(IllegalArgumentException.class, () -> service.changeUsername(contributor, usedName));

        // Success
        String newName = "ac2Un38NXBvs7KrZGxwWjQ";

        assertAll(() -> service.changeUsername(contributor, newName));
        assertEquals(newName, contributor.getUsername());
    }

    /**
     * Test changing email address
     */
    @Test
    void testChangeEmail() {
        // Already used username
        String usedMail = administrator.getEmail();

        assertThrows(IllegalArgumentException.class, () -> service.changeEmail(contributor, usedMail));

        // Invalid email
        String invalidMail = "ac2Un38NXBvs7KrZGxwWjQ";
        
        assertThrows(IllegalArgumentException.class, () -> service.changeEmail(contributor, invalidMail));

        // Success
        String newMail = "ac2Un38NXBvs@7KrZGxwW.jQ";

        assertAll(() -> service.changeEmail(contributor, newMail));
        assertEquals(newMail, contributor.getEmail());
    }

    /**
     * Test changing password
     */
    @Test
    void testChangePassword() {
        String newPassword = "H3ll0W0rld!";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        assertAll(() -> service.changePassword(contributor, newPassword));
        assertTrue(encoder.matches(newPassword, contributor.getPassword()));        
    }

    /**
     * Test changing role
     */
    @Test
    void testChangeRole() {
        // Unknown role
        assertThrows(IllegalArgumentException.class, () -> service.changeRole(contributor, "GOD", administrator));

        // Contributor can't change role
        assertThrows(AuthorizationDeniedException.class, () -> service.changeRole(contributor, User.ADMINISTRATOR, contributor));
        assertThrows(AuthorizationDeniedException.class, () -> service.changeRole(contributor, User.SUPERADMINISTRATOR, contributor));

        // Admin can't become superadmin
        assertThrows(AuthorizationDeniedException.class, () -> service.changeRole(administrator, User.SUPERADMINISTRATOR, administrator));

        // Success
        assertAll(() -> service.changeRole(contributor, User.ADMINISTRATOR, superadministrator));
        assertEquals(User.ADMINISTRATOR, contributor.getRole());

        assertAll(() -> service.changeRole(contributor, User.CONTRIBUTOR, superadministrator));
        assertEquals(User.CONTRIBUTOR, contributor.getRole());

        assertAll(() -> service.changeRole(administrator, User.SUPERADMINISTRATOR, superadministrator));
        assertEquals(User.SUPERADMINISTRATOR, administrator.getRole());
    }
}
