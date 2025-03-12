package com.cartowiki.webapp.authentication.model;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * Data template for sign up requests
 */
public class SignUpRequest {
    public static final String EMPTY_ARGUMENT = "";

    @Schema(name = "username", example = "cartowiki", requiredMode = RequiredMode.REQUIRED)
    private String username;

    @Schema(name = "email", example = "contributor@cartowiki.com", requiredMode = RequiredMode.REQUIRED)
    private String email;

    @Schema(name = "password", example = "Str0ngP@ssw0rd!", requiredMode = RequiredMode.REQUIRED)
    private String password;
    
    /**
     * No-argument constructor
     */
    public SignUpRequest() {
        this.username = EMPTY_ARGUMENT;
        this.email = EMPTY_ARGUMENT;
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

    /**
     * Test if email address is not set
     * @return Is email address not set
     */
    public boolean checkForEmptyEmail() {
        return this.email.equals(EMPTY_ARGUMENT);
    }
}
