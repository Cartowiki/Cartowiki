package com.cartowiki.webapp.authentication.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.cartowiki.webapp.authentication.filter.JwtAuthFilter;
import com.cartowiki.webapp.users.model.User;
import com.cartowiki.webapp.users.service.UserService;

/**
 * Spring Security configurations
 */
@Configuration
@ConfigurationProperties(prefix = "security")
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private JwtAuthFilter authFilter;
    private UserService userService;

    @Value("${security.cors_url}")
    private String corsUrl;

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
                // Public endpoints
                .requestMatchers("/auth/signup", "/auth/login", "/api/**").permitAll()

                // User management (ADMINISTRATOR only)
                .requestMatchers(HttpMethod.GET, "/users").hasRole(User.ADMINISTRATOR)
                .requestMatchers(HttpMethod.GET, "/users/{id}").hasRole(User.ADMINISTRATOR)
                .requestMatchers(HttpMethod.PUT, "/users/{id}").hasRole(User.ADMINISTRATOR)
                .requestMatchers(HttpMethod.DELETE, "/users/{id}").hasRole(User.ADMINISTRATOR)

                // Contribution management
                .requestMatchers(HttpMethod.GET, "/contributions").hasRole(User.CONTRIBUTOR)
                .requestMatchers(HttpMethod.GET, "/contributions/{id}").hasRole(User.CONTRIBUTOR)
                .requestMatchers(HttpMethod.POST, "/contributions").hasRole(User.CONTRIBUTOR)
                .requestMatchers(HttpMethod.PUT, "/contributions/{id}").hasRole(User.CONTRIBUTOR)
                .requestMatchers(HttpMethod.DELETE, "/contributions/{id}").hasRole(User.CONTRIBUTOR)

                // Contribution validation (ADMINISTRATOR only)
                .requestMatchers(HttpMethod.POST, "/contributions/{id}/validate").hasRole(User.ADMINISTRATOR)

                // Protect all other endpoints
                .anyRequest().authenticated()
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
        String hierarchy = "ROLE_" + User.SUPERADMINISTRATOR + " > ROLE_" + User.ADMINISTRATOR + " > ROLE_" + User.CONTRIBUTOR;

        return RoleHierarchyImpl.fromHierarchy(hierarchy);
    }

    /**
     * Create filter to allowed Cross-Origin Resources Sharing (CORS)
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();

        System.out.println("CORS URL: " + this.corsUrl);

        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList(this.corsUrl));
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
        
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    /**
     * Get Cross-origin Ressources Sharing (CORS) allowed origin
     * @return Cross-origin Ressources Sharing (CORS) allowed origin
     */
    public String getCorsUrl() {
        return corsUrl;
    }

    /**
     * Set Cross-origin Ressources Sharing (CORS) allowed origin
     * @param corsUrl New Cross-origin Ressources Sharing (CORS) allowed origin
     */
    public void setCorsUrl(String corsUrl) {
        this.corsUrl = corsUrl;
    }
}
