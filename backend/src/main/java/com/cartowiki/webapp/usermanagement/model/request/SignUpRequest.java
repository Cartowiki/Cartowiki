package com.cartowiki.webapp.usermanagement.model.request;

/**
 * Data template for sign up requests
 */
public class SignUpRequest {
    private String username;
    private String mail;
    private String password;
    
    /**
     * No-argument constructor
     */
    public SignUpRequest() {
        this.username = "";
        this.mail = "";
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
     * Mail address getter
     * @return Current mail address
     */
    public String getMail() {
        return mail;
    }

    /**
     * Mail address setter
     * @param mail New mail address
     */
    public void setMail(String mail) {
        this.mail = mail;
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
