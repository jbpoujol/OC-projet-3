package com.openclassrooms.projet3.controller;

import com.openclassrooms.projet3.dtos.UserDTO;
import com.openclassrooms.projet3.service.DBUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class DBUserController {
    private final DBUserService dbUserService;

    public DBUserController(DBUserService dbUserService) {
        this.dbUserService = dbUserService;
    }

    /**
     * Retrieves detailed information about a specific user by their unique identifier (ID).
     * <p>
     * This method fetches a user based on the provided ID and maps the retrieved entity to a {@link UserDTO}
     * which includes user's ID, name, email, and timestamps for account creation and last update. It serves
     * as a straightforward way to access individual user information for client-side applications.
     * <p>
     * <strong>Path Variable:</strong> {@code id} - The unique identifier of the user to retrieve.
     *
     * <strong>Successful Response:</strong>
     * <ul>
     *     <li><b>200 OK:</b> Returns a {@link UserDTO} containing the user's details.</li>
     * </ul>
     *
     * <strong>Error Responses:</strong>
     * <ul>
     *     <li><b>404 Not Found:</b> No user could be found for the provided ID.</li>
     * </ul>
     *
     * @param id The ID of the user to retrieve.
     * @return A {@link ResponseEntity} containing the {@link UserDTO} of the requested user if found,
     * or a 404 Not Found status if the user does not exist.
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
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return dbUserService.findUserById(id)
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
