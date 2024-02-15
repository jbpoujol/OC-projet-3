package com.openclassrooms.projet3.model;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request body for user registration containing user details")
public class RegistrationRequest {

    @Schema(description = "Full name of the user", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Email address of the user", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Password for the user account", example = "SecurePassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}