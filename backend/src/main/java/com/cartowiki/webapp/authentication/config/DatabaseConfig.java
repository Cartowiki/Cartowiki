package com.cartowiki.webapp.authentication.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Store field restrictions of database
 */
@Configuration
@ConfigurationProperties(prefix = "database.restrictions")
public class DatabaseConfig {
    private int usernameMaxLength;
    private int emailMaxLength;
    private int passwordMaxLength;

    /**
     * Username max length getter
     * @return Usernmae max length
     */
    public int getUsernameMaxLength() {
        return usernameMaxLength;
    }

    /**
     * Username max length setter
     * @param usernameMaxLength New username max length
     */
    public void setUsernameMaxLength(int usernameMaxLength) {
        this.usernameMaxLength = usernameMaxLength;
    }

    /**
     * Email max length getter
     * @return Email max length
     */
    public int getEmailMaxLength() {
        return emailMaxLength;
    }

    /**
     * Email max length setter
     * @param emailMaxLength New email max length
     */
    public void setEmailMaxLength(int emailMaxLength) {
        this.emailMaxLength = emailMaxLength;
    }

    /**
     * Password max length getter
     * @return Password max length
     */
    public int getPasswordMaxLength() {
        return passwordMaxLength;
    }

    /**
     * Password max length setter
     * @param passwordMaxLength New password max length
     */
    public void setPasswordMaxLength(int passwordMaxLength) {
        this.passwordMaxLength = passwordMaxLength;
    }
}
