package com.cartowiki.webapp.authentication.model;

/**
 * Data template for log in requests
 */
public class LogInRequest {
    private String username;
    private String password;
    
    /**
     * No-argument constructor
     */
    public LogInRequest() {
        this.username = "";
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
