package com.openclassrooms.projet3.service;

import com.openclassrooms.projet3.model.DBUser;

import java.util.Optional;

public interface DBUserService {

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

