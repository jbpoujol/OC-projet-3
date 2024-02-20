package com.openclassrooms.projet3.dtos;

import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object representing a user.
 * <p>
 * This class encapsulates user data transferred between the client and the server.
 * It includes basic user identification and metadata.
 */
@Data
public class UserDTO {

    /**
     * The unique identifier of the user.
     */
    private Long id;

    /**
     * The name of the user.
     */
    private String name;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The date when the user account was created.
     */
    private LocalDate created_at;

    /**
     * The date when the user account was last updated.
     */
    private LocalDate updated_at;

    /**
     * Constructs a new UserDTO with specified details.
     *
     * @param id         The unique identifier of the user.
     * @param name       The name of the user.
     * @param email      The email address of the user.
     * @param created_at The date of account creation.
     * @param updated_at The date of the last update to the account.
     */
    public UserDTO(Long id, String name, String email, LocalDate created_at, LocalDate updated_at) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
}