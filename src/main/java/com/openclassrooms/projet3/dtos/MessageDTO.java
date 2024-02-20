package com.openclassrooms.projet3.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object for conveying message details between the client and server.
 * <p>
 * This class is used to encapsulate the data of a message related to a rental property,
 * including identifiers for the rental and the user, as well as the message content itself.
 */
@Data
public class MessageDTO {

    /**
     * The unique identifier of the rental property the message pertains to.
     * <p>
     * This field cannot be null and is validated to ensure it is provided.
     */
    @NotNull(message = "Rental ID cannot be null")
    private Long rental_id;

    /**
     * The unique identifier of the user who is sending the message.
     * <p>
     * This field cannot be null and is validated to ensure it is provided.
     */
    @NotNull(message = "User ID cannot be null")
    private Long user_id;

    /**
     * The content of the message being sent.
     * <p>
     * This field cannot be blank and is validated to ensure it contains meaningful content.
     */
    @NotBlank(message = "Message cannot be blank")
    private String message;
}
