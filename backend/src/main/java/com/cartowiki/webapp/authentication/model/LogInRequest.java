package com.cartowiki.webapp.authentication.model;

/**
 * Data template for log in requests
 */
public class LogInRequest {
    public static final String EMPTY_ARGUMENT = "";

    private String username;
    private String password;
    
    /**
     * No-argument constructor
     */
    public LogInRequest() {
        this.username = EMPTY_ARGUMENT;
        this.password = EMPTY_ARGUMENT;
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

    /**
     * Test if username is not set
     * @return Is username not set
     */
    public boolean isUsernameEmpty() {
        return this.username.equals(EMPTY_ARGUMENT);
    }

    /**
     * Test if password is not set
     * @return Is password not set
     */
    public boolean isPasswordEmpty() {
        return this.password.equals(EMPTY_ARGUMENT);
    }
}
