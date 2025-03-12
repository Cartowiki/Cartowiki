package com.cartowiki.webapp.authentication.controller;

import java.util.Optional;

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

import com.cartowiki.webapp.users.config.DatabaseConfig;
import com.cartowiki.webapp.users.model.User;
import com.cartowiki.webapp.users.repository.UserRepository;
import com.cartowiki.webapp.util.ResponseMaker;

/**
 * Tests requests for authentication
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class AuthenticationControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository repository;

    @Autowired
    DatabaseConfig config;

    User user;
    User newUser;

    String userPassword = "XUeZ2tfqT9S4vYr3BECGnM";

    /**
     * Add mock users to database for tests
     */
    @BeforeAll
    void addMockUsers() {
        BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();

        // Only user password is encoded because it's the only one which will be checked
        user = repository.save(new User("XUeZ2tfqT9S4vYr3BECGnM", "XUeZ2tfqT9S4vYr@3BE.CGnM", bCryptEncoder.encode(userPassword), 1));
        newUser = new User("XUeZ2tfqT9S4v", "XUeZ2tf@qTS.fv", "XUeZ2tfqT9S4v", 0);
    }

    /**
     * Remove mock users
     */
    @AfterAll
    void removeMockUsers() {
        repository.delete(user);

        Optional<User> optNewUser = repository.findByUsername(newUser.getUsername());

        if (optNewUser.isPresent()) {
                repository.delete(optNewUser.get());
        }
    }

    /**
     * Test log in endpoint
     * @throws Exception Error during results matching
     */
    @Test
    void testAuthenticateAndGetToken() throws Exception{
        String url = "/auth/login";

        // Foreign request
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"field1\":\"value\", \"filed2\":\"value\", \"filed3\":\"value\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.MESSAGE).value("Missing username"));

        // Missing username
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.MESSAGE).value("Missing username"));

        // Missing password
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"" + user.getUsername() + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.MESSAGE).value("Missing password"));

        // Bad credentials 
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"" + user.getUsername() + "\", \"password\": \"XUeZ2\"}"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.MESSAGE).value("Invalid credentials"));

        // Valid logging in
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"" + user.getUsername() + "\", \"password\": \"" + userPassword + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.TOKEN).exists());
    }

    /**
     * Test sign up endpoint
     * @throws Exception Error during results matching
     */
    @Test
    void testSignUp() throws Exception {
        String url = "/auth/signup";

        // Foreign request
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"field1\":\"value\", \"filed2\":\"value\", \"filed3\":\"value\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.MESSAGE).value("Missing username"));

        // Missing username
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.MESSAGE).value("Missing username"));

        // Missing email
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"" + user.getUsername() + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.MESSAGE).value("Missing email"));

        // Missing password
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"" + user.getUsername() + "\", \"email\": \"" + user.getEmail() + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.MESSAGE).value("Missing password"));

        // Username is too long
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"" + "A".repeat(config.getUsernameMaxLength() + 1) + "\", \"email\": \"" + user.getEmail() + "\", \"password\":\"pass\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.MESSAGE).value("Username is too long"));

        // Email is too long
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"" + user.getUsername() + "\", \"email\": \"" + "A".repeat(config.getEmailMaxLength() + 1) + "\", \"password\":\"pass\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.MESSAGE).value("Email is too long"));

        // Username is taken [Error]
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"" + user.getUsername() + "\", \"email\": \"" + user.getEmail() + "\", \"password\":\"" + user.getPassword() + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.MESSAGE).value("Username is already taken"));

        // Invalid email
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"XUeZ2tfqT9S4vYr3\", \"email\": \"" + "A".repeat(config.getEmailMaxLength()) + "\", \"password\":\"" + user.getPassword() + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.MESSAGE).value("Email address is not valid"));

        // Email is taken [Error]
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"XUeZ2tfqT9S4vYr3\", \"email\": \"" + user.getEmail() + "\", \"password\":\"" + user.getPassword() + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + ResponseMaker.MESSAGE).value("Email address is already taken"));

        // Valid user
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"" + newUser.getUsername() + "\", \"email\": \"" + newUser.getEmail() + "\", \"password\":\"" + newUser.getPassword() + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
}
