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

    private String pseudo;
    private String mail;

    /**
     * No-argument constructor
     */
    public User() {
        this.id = -1;
    }

    /**
     * Full-argument constructor
     * @param id User's id
     * @param password User's password
     * @param adminLevel User's adminstrator level
     * @param pseudo User's pseudonym
     * @param mail User's mail address
     */
    public User(int id, String password, int adminLevel, String pseudo, String mail) {
        this.id = id;
        this.password = password;
        this.adminLevel = adminLevel;
        this.pseudo = pseudo;
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
     * User's pseudonym getter
     * @return User's pseudonym
     */
    public String getPseudo() {
        return pseudo;
    }

    /**
     * User's pseudonym setter
     * @param pseudo New pseudonym
     */
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
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
