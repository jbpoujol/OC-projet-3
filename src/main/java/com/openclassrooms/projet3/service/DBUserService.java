package com.openclassrooms.projet3.service;

import com.openclassrooms.projet3.dtos.UserDTO;
import com.openclassrooms.projet3.excepton.CustomNotFoundException;
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

    /**
     * Retrieves a user by their unique ID and converts the user to a {@link UserDTO}.
     * <p>
     * This method searches for a user in the database using the provided unique identifier. If the user is found,
     * it is converted into a {@link UserDTO} object that contains selected information about the user, suitable for
     * external use. This DTO can be used to hide certain user details or to transform the data into a format that is
     * more convenient for clients of the service.
     * <p>
     * The conversion process ensures that sensitive information is not exposed and that the data is presented in a
     * consistent and easily consumable format.
     *
     * @param id The unique identifier of the user to be retrieved.
     * @return A {@link UserDTO} containing the user's details.
     * @throws CustomNotFoundException if no user with the provided ID can be found in the database. This exception is used to signal to the caller that the requested operation could not be completed due to missing data.
     */
    UserDTO findUserDTOById(Long id);


}

