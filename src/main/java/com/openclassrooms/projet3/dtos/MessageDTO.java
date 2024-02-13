package com.openclassrooms.projet3.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageDTO {
    @NotNull(message = "Rental ID cannot be null")
    private Long rental_id;

    @NotNull(message = "User ID cannot be null")
    private Long user_id;

    @NotBlank(message = "Message cannot be blank")
    private String message;
}
