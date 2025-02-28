package com.cartowiki.webapp.users.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
}
