package com.cartowiki.webapp.users.model;

/**
 * User edit request
 */
public class EditRequest {
    public static final String EMPTY_ARGUMENT = "";

    private String username;
    private String email;
    private String password;
    private String role;

    /**
     * Default constructor
     */
    public EditRequest() {
        this.username = EMPTY_ARGUMENT;
        this.email = EMPTY_ARGUMENT;
        this.password = EMPTY_ARGUMENT;
        this.role = EMPTY_ARGUMENT;
    }

    /**
     * Username getter
     * @return Username
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
     * @return Email address
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
     * @return Password
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
     * Role getter
     * @return Role
     */
    public String getRole() {
        return role;
    }

    /**
     * Role setter
     * @param role New role
     */
    public void setRole(String role) {
        this.role = role;
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

    /**
     * Test if role is not set
     * @return Is role not set
     */
    public boolean checkForEmptyRole() {
        return this.role.equals(EMPTY_ARGUMENT);
    }
}
