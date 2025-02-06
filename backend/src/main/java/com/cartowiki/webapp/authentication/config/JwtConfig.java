package com.cartowiki.webapp.authentication.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Store parameters for JWT authentication
 */
@Configuration
@ConfigurationProperties(prefix = "app.authentication.jwt")
public class JwtConfig {
    private String secret;
    private int expirationTime;

    /**
     * JWT secret getter
     * @return JWT secret
     */
    public String getSecret() {
        return secret;
    }

    /**
     * JWT secret setter
     * @param secret New JWT secret
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * JWT expiration time getter
     * @return JWT expiration time (in milliseconds)
     */
    public int getExpirationTime() {
        return expirationTime;
    }

    /**
     * JWT expiration time setter
     * @param expirationTime New JWT expiration time (in milliseconds)
     */
    public void setExpirationTime(int expirationTime) {
        this.expirationTime = expirationTime;
    }    
}
