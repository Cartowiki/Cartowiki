package com.cartowiki.webapp.users.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.cartowiki.webapp.authentication.service.JwtService;
import com.cartowiki.webapp.users.model.User;
import com.cartowiki.webapp.users.repository.UserRepository;
import com.cartowiki.webapp.util.ResponseMaker;


/**
 * Tests on users management controller
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class UsersControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository repository;

    @Autowired
    JwtService jwtService;

    User deletedUser;
    User modifiedUser;
    User contributor;
    User administrator;
    User superadministrator;

    String contributorToken;
    String administratorToken;
    String superadministratorToken;

    /**
     * Add mock users to the database for test
     */
    @BeforeAll
    void addMockUsers() {
        deletedUser = repository.save(new User("A5NUhq", "FgVw@EKR.fc", "P@ssword", 0, false));
        modifiedUser = repository.save(new User("qzrger", "rgzeg@EKR.fc", "P@ssword", 0));

        contributor = repository.save(new User("A5NUhqTH3x", "Fna2gVw@EKR.fc", "P@ssword", 0));
        administrator = repository.save(new User("jQDqkGMa7HEm", "zt9RsVd@P83.eg", "P@ssword", 1));
        superadministrator = repository.save(new User("cX3q9vda5", "hNx2pHjF@mTD.ws", "P@ssword", 2));

        // Get users' token
        contributorToken = jwtService.generateToken(contributor.getUsername());
        administratorToken = jwtService.generateToken(administrator.getUsername());
        superadministratorToken = jwtService.generateToken(superadministrator.getUsername());
    }

    /**
     * Remove mock users
     */
    @AfterAll
    void removeMockUsers() {
        repository.delete(deletedUser);
        repository.delete(modifiedUser);

        repository.delete(contributor);
        repository.delete(administrator);
        repository.delete(superadministrator);
    }

    /**
     * Test getting user public data
     */
    @Test
    void testGetUser() throws Exception {
        String url = "/users/";

        // Forbidden access (by contributor)
        mockMvc.perform(MockMvcRequestBuilders.get(url + "1")
                        .header("Authorization", "Bearer " + contributorToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        // Non-existing user (id -1)
        mockMvc.perform(MockMvcRequestBuilders.get(url + "-1")
                        .header("Authorization", "Bearer " + administratorToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.MESSAGE).value("Missing user"));

        // Missing priviledge (by administrator on superadministrator)
        mockMvc.perform(MockMvcRequestBuilders.get(url + String.valueOf(superadministrator.getId()))
                        .header("Authorization", "Bearer " + administratorToken))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.MESSAGE).value("Missing priviledge"));

        // Success (by superadministrator on administrator)
        mockMvc.perform(MockMvcRequestBuilders.get(url + String.valueOf(administrator.getId()))
                        .header("Authorization", "Bearer " + superadministratorToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.DATA + ".username").value(administrator.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.DATA + ".email").value(administrator.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.DATA + ".role").value(administrator.getRole()));
    }

    /**
     * Test getting all users' public data with less or equal priviledge
     */
    @Test
    void testGetAllLessPriviledgedUser() throws Exception {
        String url = "/users";

        // Unauthorized user
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("Authorization", "Bearer " + contributorToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
        
        // Authorized user
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("Authorization", "Bearer " + administratorToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.DATA).isArray());
    }

    /**
     * Test soft delete one user with less or equal priviledge
     */
    @Test
    void testDeleteUser() throws Exception {
        String url = "/users/";

        // Unauthorized user
        mockMvc.perform(MockMvcRequestBuilders.delete(url + String.valueOf(administrator.getId()))
                        .header("Authorization", "Bearer " + contributorToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        // Illegal request on user with more priviledge
        mockMvc.perform(MockMvcRequestBuilders.delete(url + String.valueOf(superadministrator.getId()))
                        .header("Authorization", "Bearer " + administratorToken))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        // Missing user
        mockMvc.perform(MockMvcRequestBuilders.delete(url + "-1")
                        .header("Authorization", "Bearer " + administratorToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        // Deleted user
        mockMvc.perform(MockMvcRequestBuilders.delete(url + String.valueOf(deletedUser.getId()))
                        .header("Authorization", "Bearer " + administratorToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        
        // Authorized user
        mockMvc.perform(MockMvcRequestBuilders.delete(url + String.valueOf(contributor.getId()))
                        .header("Authorization", "Bearer " + superadministratorToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.MESSAGE).value("User successfully deleted"));
    }

    /**
     * Test the validity of the request to edit user
     */
    @Test
    void testEditUser() throws Exception {
        String url = "/users/";

        // Bad request : no body 
        mockMvc.perform(MockMvcRequestBuilders.put(url + "-1")
                        .header("Authorization", "Bearer " + administratorToken))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // User not found
        mockMvc.perform(MockMvcRequestBuilders.put(url + "-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .header("Authorization", "Bearer " + administratorToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        // Requester is less priviledged than the target
        mockMvc.perform(MockMvcRequestBuilders.put(url + String.valueOf(superadministrator.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .header("Authorization", "Bearer " + administratorToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Test edit user by changing its username
     */
    @Test
    void testEditUsername() throws Exception {
        String url = "/users/";

        // Already used username
        mockMvc.perform(MockMvcRequestBuilders.put(url + String.valueOf(modifiedUser.getId()))   
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"" + administrator.getUsername() + "\"}")
                        .header("Authorization", "Bearer " + administratorToken))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        
        assertTrue(repository.findByUsername(modifiedUser.getUsername()).isPresent());

        // Username successfully changed
        modifiedUser.setUsername("hrfvqmzihvria"); // New username

        mockMvc.perform(MockMvcRequestBuilders.put(url + String.valueOf(modifiedUser.getId()))   
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"" + modifiedUser.getUsername() + "\"}")
                        .header("Authorization", "Bearer " + administratorToken))
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        assertTrue(repository.findByUsername(modifiedUser.getUsername()).isPresent());
    }

    /**
     * Test edit user by changing its email address
     */
    @Test
    void testEditEmail() throws Exception {
        String url = "/users/";

        // Already used email address
        mockMvc.perform(MockMvcRequestBuilders.put(url + String.valueOf(modifiedUser.getId()))   
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + administrator.getEmail() + "\"}")
                        .header("Authorization", "Bearer " + administratorToken))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        assertTrue(repository.findByEmail(modifiedUser.getEmail()).isPresent());

        // Invalid email address

        mockMvc.perform(MockMvcRequestBuilders.put(url + String.valueOf(modifiedUser.getId()))   
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"hrfvqmzihvria\"}")
                        .header("Authorization", "Bearer " + administratorToken))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        assertTrue(repository.findByEmail(modifiedUser.getEmail()).isPresent());

        // Email address successfully changed
        modifiedUser.setEmail("hrfvqm@zihvr.ia"); // New email

        mockMvc.perform(MockMvcRequestBuilders.put(url + String.valueOf(modifiedUser.getId()))   
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + modifiedUser.getEmail() + "\"}")
                        .header("Authorization", "Bearer " + administratorToken))
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        assertTrue(repository.findByEmail(modifiedUser.getEmail()).isPresent());

    }

    /**
     * Test edit user by changing its password
     */
    @Test
    void testEditPassword() throws Exception {
        String url = "/users/";
        String newPassword = "N3wP@ssw0rd!";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        mockMvc.perform(MockMvcRequestBuilders.put(url + String.valueOf(modifiedUser.getId()))   
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\": \"" + newPassword + "\"}")
                        .header("Authorization", "Bearer " + administratorToken))
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        assertTrue(encoder.matches(newPassword, repository.findById(modifiedUser.getId()).get().getPassword()));
    }

    /**
     * Test edit user by changing its role
     */
    @Test
    void testEditRole() throws Exception {
        String url = "/users/";

        // Unknown role
        mockMvc.perform(MockMvcRequestBuilders.put(url + String.valueOf(modifiedUser.getId()))   
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\": \"GOD\"}")
                        .header("Authorization", "Bearer " + administratorToken))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        assertEquals(modifiedUser.getAdminLevel(), repository.findById(modifiedUser.getId()).get().getAdminLevel());

        // Administrator can't change role to superadministrator
        mockMvc.perform(MockMvcRequestBuilders.put(url + String.valueOf(modifiedUser.getId()))   
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\": \"" + User.SUPERADMINISTRATOR + "\"}")
                        .header("Authorization", "Bearer " + administratorToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        assertEquals(modifiedUser.getAdminLevel(), repository.findById(modifiedUser.getId()).get().getAdminLevel());

        // Role successfully changed to administrator 
        mockMvc.perform(MockMvcRequestBuilders.put(url + String.valueOf(modifiedUser.getId()))   
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\": \"" + User.ADMINISTRATOR + "\"}")
                        .header("Authorization", "Bearer " + superadministratorToken))
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        assertEquals(User.ADMINISTRATOR_CODE, repository.findById(modifiedUser.getId()).get().getAdminLevel());

        // Role successfully changed to superadministrator 
        mockMvc.perform(MockMvcRequestBuilders.put(url + String.valueOf(modifiedUser.getId()))   
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\": \"" + User.SUPERADMINISTRATOR + "\"}")
                        .header("Authorization", "Bearer " + superadministratorToken))
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        assertEquals(User.SUPERADMINISTRATOR_CODE, repository.findById(modifiedUser.getId()).get().getAdminLevel());

        // Role successfully changed to modifiedUser 
        mockMvc.perform(MockMvcRequestBuilders.put(url + String.valueOf(modifiedUser.getId()))   
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\": \"" + User.CONTRIBUTOR + "\"}")
                        .header("Authorization", "Bearer " + superadministratorToken))
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        assertEquals(User.CONTRIBUTOR_CODE, repository.findById(modifiedUser.getId()).get().getAdminLevel());
    }
}
