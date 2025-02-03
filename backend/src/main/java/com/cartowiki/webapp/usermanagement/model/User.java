package com.cartowiki.webapp.usermanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Class for the User entity of the database
 */
@Entity
@Table(name = "Utilisateurs")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private int id;

    @Column(name = "mdp_hash")
    private String password;

    @Column(name = "niveau_admin")
    private int adminLevel;

    @Column(name = "pseudo")
    private String username;

    private String mail;

    /**
     * No-argument constructor
     */
    public User() {
        // All variables are set to their type's default value
    }

    /**
     * Constructor with arguments
     * @param username Username
     * @param mail User's mail address
     * @param password User's password
     * @param adminLevel User's adminstrator level
     */
    public User(String username, String mail, String password, int adminLevel) {
        this.password = password;
        this.adminLevel = adminLevel;
        this.username = username;
        this.mail = mail;
    }

    /**
     * User's id getter
     * @return User's id
     */
    public int getId() {
        return id;
    }

    /**
     * User's id setter
     * @param id New id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * User's password getter
     * @return User's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * User's password setter
     * @param password New password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * User's administrator level getter
     * @return User's administrator level
     */
    public int getAdminLevel() {
        return adminLevel;
    }

    /**
     * User's administrator level setter
     * @param adminLevel New administrator level
     */
    public void setAdminLevel(int adminLevel) {
        this.adminLevel = adminLevel;
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
     * User's mail address getter
     * @return User's mail address
     */
    public String getMail() {
        return mail;
    }

    /**
     * User's mail address setter
     * @param mail New mail address
     */
    public void setMail(String mail) {
        this.mail = mail;
    }
}
