package com.cartowiki.webapp.users.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cartowiki.webapp.users.model.User;

/**
 * Requests management about the User entity in the database
 */
@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    /**
     * Search a user by its username
     * @param username Username
     * @return Optional user, might be empty if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Search a user by its email
     * @param email Email address
     * @return Optional user, might be empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Return the list of all users with certains roles
     * @param adminLevels List of accepted roles
     * @return List of users
     */
    List<User> findAllByAdminLevelIn(Collection<Integer> adminLevels);
}
