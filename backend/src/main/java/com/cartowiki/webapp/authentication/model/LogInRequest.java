package com.cartowiki.webapp.authentication.model;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * Data template for log in requests
 */
public class LogInRequest {
    public static final String EMPTY_ARGUMENT = "";

    @Schema(name = "username", example = "cartowiki", requiredMode = RequiredMode.REQUIRED)
    private String username;

    @Schema(name = "password", example = "Str0ngP@ssw0rd!", requiredMode = RequiredMode.REQUIRED)
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
    public boolean checkForEmptyUsername() {
        return this.username.equals(EMPTY_ARGUMENT);
    }

    /**
     * Test if password is not set
     * @return Is password not set
     */
    public boolean checkForEmptyPassword() {
        return this.password.equals(EMPTY_ARGUMENT);
    }
}
