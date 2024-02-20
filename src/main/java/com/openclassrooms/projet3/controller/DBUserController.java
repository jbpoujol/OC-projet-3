package com.openclassrooms.projet3.controller;

import com.openclassrooms.projet3.dtos.UserDTO;
import com.openclassrooms.projet3.excepton.CustomNotFoundException;
import com.openclassrooms.projet3.service.DBUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class DBUserController {
    private final DBUserService dbUserService;

    public DBUserController(DBUserService dbUserService) {
        this.dbUserService = dbUserService;
    }

    /**
     * Retrieves the details of a user identified by their unique ID.
     * <p>
     * This endpoint fetches the user's information from the database based on the provided user ID. If found,
     * it returns the user details including ID, name, email, and timestamps for account creation and last update.
     * The data is returned as a {@link UserDTO} object. This method serves as a direct way to access a user's
     * information by their unique identifier.
     * <p>
     * Swagger/OpenAPI annotations ({@code @Operation} and {@code @ApiResponse}) provide additional documentation
     * for the API, including the expected HTTP response codes and example response bodies.
     *
     * @param id The unique identifier of the user to retrieve.
     * @return A {@link ResponseEntity} containing the {@link UserDTO} if the user is found, or a 404 Not Found
     * status if the user does not exist in the database.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "id": 1,
                                                "name": "John Doe",
                                                "email": "john.doe@example.com",
                                                "createdAt": "2020-01-01T00:00:00Z",
                                                "updatedAt": "2020-01-02T00:00:00Z"
                                            }
                                            """))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                                "error": "User not found"
                                            }
                                            """)))
            })
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            UserDTO userDTO = dbUserService.findUserDTOById(id);
            return ResponseEntity.ok(userDTO);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
