package com.openclassrooms.projet3.service;

import com.openclassrooms.projet3.model.DBUser;

import java.util.Optional;

public interface DBUserService {

    /**
     * Registers a new user with the provided details.
     * <p>
     * This method creates a new {@link DBUser} entity, encodes the password using
     * BCryptPasswordEncoder, and saves the user to the database.
     *
     * @param name     The name of the user.
     * @param email    The email address of the user, which must be unique.
     * @param password The plaintext password to be encrypted and stored.
     * @return The newly created {@link DBUser} entity.
     */
    DBUser registerUser(String name, String email, String password);

    /**
     * Finds a user by their email address.
     *
     * @param email The email address of the user to find.
     * @return An {@link Optional} containing the {@link DBUser} if found, or empty if no user exists with the given email.
     */
    Optional<DBUser> find(String email);

    /**
     * Finds a user by their unique identifier.
     *
     * @param id The ID of the user to find.
     * @return An {@link Optional} containing the {@link DBUser} if found, or empty if no user exists with the given ID.
     */
    Optional<DBUser> findUserById(Long id);
}

