package com.cartowiki.webapp.authentication.model;

/**
 * Data template for sign up requests
 */
public class SignUpRequest {
    private String username;
    private String email;
    private String password;
    
    /**
     * No-argument constructor
     */
    public SignUpRequest() {
        this.username = "";
        this.email = "";
        this.password = "";
    }

    /**
     * Username getter
     * @return Current username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Username setter
     * @param username New username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Email address getter
     * @return Current email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Email address setter
     * @param email New email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Password getter
     * @return Current password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Password setter
     * @param password New password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
