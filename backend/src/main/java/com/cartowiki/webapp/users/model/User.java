package com.cartowiki.webapp.users.model;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
public class User implements UserDetails{
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

    @Column(name = "mail")
    private String email;

    /**
     * No-argument constructor
     */
    public User() {
        // All variables are set to their type's default value
    }

    /**
     * Constructor with arguments
     * @param username Username
     * @param email User's email address
     * @param password User's password
     * @param adminLevel User's adminstrator level
     */
    public User(String username, String email, String password, int adminLevel) {
        this.password = password;
        this.adminLevel = adminLevel;
        this.username = username;
        this.email = email;
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
    @Override
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
    @Override
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
     * User's email address getter
     * @return User's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * User's email address setter
     * @param email New email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Determine roles of the user depending on the adminLevel
     * @return Collection of roles
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<? extends GrantedAuthority> authorization;

        switch (adminLevel) {
            case 0:
                authorization = Collections.singletonList(new SimpleGrantedAuthority("ROLE_CONTRIBUTOR"));
                break;
        
            case 1:
                authorization = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
                break;
            
            case 2:
                authorization = Collections.singletonList(new SimpleGrantedAuthority("ROLE_SUPERADMINISTRATOR"));
                break;

            default:
                authorization = Collections.emptyList();
        }
        
        return authorization;
    }

    /**
     * Check if the account is not expired (no expiration yet)
     * @return Is the account not expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Check if the account is not locked (no lock yet)
     * @return Is the account not locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Check f the account's credentials are not expired (no expiration yet)
     * @return Is the credential not expired
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Check if the account is enabled
     * @return Is the account enabled
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
