package com.scoreDEI.Repositories;

import com.scoreDEI.Entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {

    /**
     * Find a user by email and password and return it as an Optional.
     *
     * @param email The email of the user
     * @param password The password of the user.
     * @return Optional<User>
     */
    @Query("SELECT u FROM User u WHERE u.email = ?1 AND u.password = ?2")
    public Optional<User> findUserByEmailAndPassword(String email, String password);

}
