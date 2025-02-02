package com.cartowiki.webapp.usermanagement.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cartowiki.webapp.usermanagement.model.User;

/**
 * Requests management about the User entity in the database
 */
@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

}
