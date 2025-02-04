package com.cartowiki.webapp.authentication.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cartowiki.webapp.authentication.filter.JwtAuthFilter;
import com.cartowiki.webapp.authentication.service.UserService;

/**
 * Spring Security configurations
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private JwtAuthFilter authFilter;
    private UserService userService;

    /**
     * Autowired constructor
     * @param authFilter Filter chain for JWT authentication
     * @param userService Service for user management
     */
    @Autowired
    public SecurityConfig(JwtAuthFilter authFilter, UserService userService) {
        this.authFilter = authFilter;
        this.userService = userService;
    }

    /**
     * Return a new instance of UserService
     * @return Service for User beans
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return this.userService;
    }

    /**
     * Build the filter chain for security
     * @param http HttpSecurity
     * @return Built HttpSecurity
     * @throws Exception Error during the build
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/contributions", "/contributions/*", "/api/**").hasRole("CONTRIBUTOR")
                .requestMatchers("/contributions/**","/users/**").hasRole("ADMINISTRATOR")
                .anyRequest().authenticated() // Protect all other endpoints
            )
            .sessionManagement(sess -> sess
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No sessions
            )
            .authenticationProvider(authenticationProvider()) // Custom authentication provider
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter

        return http.build();
    }

    /**
     * Return a new instance for password encryption (here BCrypt)
     * @return BCrypt encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure and return an authentication provider
     * @return Configured authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Return an authentication manager from the configuration
     * @param config Authentication configuration
     * @return Authentication manager
     * @throws Exception Error when return the manager from the configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Defining roles hierarchy
     * @return Role hierarchy
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        String hierarchy = "ROLE_ADMINISTRATOR > ROLE_CONTRIBUTOR";

        return RoleHierarchyImpl.fromHierarchy(hierarchy);
    }
}
