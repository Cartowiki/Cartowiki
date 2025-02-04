package com.cartowiki.webapp.authentication.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.cartowiki.webapp.authentication.config.JwtConfig;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

/**
 * Service for JWT Authentication
 */
@Service
public class JwtService {
    private JwtConfig jwtConfig;
    private SecretKey signingKey;

    /**
     * Autowired constructor
     * @param jwtConfig JWT parameters
     */
    @Autowired
    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.signingKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    /**
     * Generate token with given user name
     * @param userName User's name
     * @return Token
     */
    public String generateToken(String userName) {        
        return createToken(userName, jwtConfig.getExpirationTime());
    }

    /**
     * Generate token with given user name and custom expiration time
     * @param userName User's name
     * @param expirationTime Time until expiration
     * @return Token
     */
    public String generateToken(String userName, long expirationTime) {        
        return createToken(userName, expirationTime);
    }

    /**
     * Create a JWT token with specified claims and subject (user name)
     * @param userName User's name
     * @param expirationTime Time until expiration
     * @return JWT token
     */
    private String createToken(String userName, long expirationTime) {
        return Jwts.builder()
               .subject(userName)
               .issuedAt(new Date(System.currentTimeMillis()))
               .expiration(new Date(System.currentTimeMillis() + expirationTime))
               .signWith(signingKey)
               .compact();
    }

    /**
     * Extract the username from the token
     * @param token token
     * @return User's name
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract the expiration date from the token
     * @param token JWT token
     * @return Expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract a claim from the token
     * @param token JWT token
     * @param claimsResolver Claims resolver
     * @return Claim
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from the token
     * @param token JWT token
     * @return All claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                   .verifyWith(signingKey)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }

    /**
     * Check if the token is expired
     * @param token JWT token
     * @return Flag if the flag is expired
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate the token against user details and expiration
     * @param token JWT token
     * @param userDetails User's details
     * @return Flag if the token is valide
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
