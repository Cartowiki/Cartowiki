package com.cartowiki.webapp.authentication.filter;

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

import com.cartowiki.webapp.authentication.model.User;
import com.cartowiki.webapp.authentication.repository.UserRepository;
import com.cartowiki.webapp.authentication.service.JwtService;

/**
 * Test the filter chain element checking JWT authentication
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class JwtAuthFilterTests {
    @Autowired
    JwtService service;

    @Autowired
    UserRepository repository;

    @Autowired
    MockMvc mockMvc;

    User contributor;
    User administrator;

    /**
     * Add mock users to database for tests
     */
    @BeforeAll
    void addMockUsers() {
        contributor = repository.save(new User("john", "john@domain.fr", "secret", 0));
        administrator = repository.save(new User("admin", "admin@admin.net", "admin", 1));
    }

    /**
     * Remove mock users
     */
    @AfterAll
    void removeMockUsers() {
        repository.delete(contributor);
        repository.delete(administrator);
    }

    /**
     * Test the authentication as being not authentified
     * @throws Exception Error during results matching
     */
    @Test
    void testNotAuthentified() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/"))
               .andExpect(MockMvcResultMatchers.status().isForbidden());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/"))
               .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Test the authentication as using another authentication than JWT
     * @throws Exception Error during results matching
     */
    @Test
    void testNotJWT() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/")
                        .header("Authorization", "Basic de=="))
               .andExpect(MockMvcResultMatchers.status().isForbidden());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/")
                        .header("Authorization", "Basic de=="))
               .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Test the authentication as being not authentified
     * @throws Exception Error during results matching
     */
    @Test
    void testContributor() throws Exception{
        String token = service.generateToken(contributor.getUsername());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/")
                        .header("Authorization", "Bearer " + token))
               .andExpect(MockMvcResultMatchers.status().isNotFound()); // Modify HTTP status if endpoint enabled

        mockMvc.perform(MockMvcRequestBuilders.get("/users/")
                        .header("Authorization", "Bearer " + token))
               .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Test the authentication with an expired token
     * @throws Exception Error during results matching
     */
    @Test
    void testExpiredToken() throws Exception{
        String token = service.generateToken(contributor.getUsername(), 0);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/")
                        .header("Authorization", "Bearer " + token))
               .andExpect(MockMvcResultMatchers.status().isForbidden());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/")
                        .header("Authorization", "Bearer " + token))
               .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Test the authentication as being not 
     * @throws Exception Error during results matching
     */
    @Test
    void testAdministrator() throws Exception{
        String token = service.generateToken(administrator.getUsername());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/")
                        .header("Authorization", "Bearer " + token))
               .andExpect(MockMvcResultMatchers.status().isNotFound()); // Modify HTTP status if endpoint enabled

        mockMvc.perform(MockMvcRequestBuilders.get("/users/")
                        .header("Authorization", "Bearer " + token))
               .andExpect(MockMvcResultMatchers.status().isNotFound()); // Modify HTTP status if endpoint enabled
    }
}
