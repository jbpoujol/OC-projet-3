package com.openclassrooms.projet3.controller;

import com.openclassrooms.projet3.dtos.UserDTO;
import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.service.DBUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class DBUserController {
    @Autowired
    private DBUserService dbUserService;

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
        // Validation de l'ID si nécessaire (exemple : vérifier si l'ID est positif)

        Optional<DBUser> userOptional = dbUserService.findUserById(id);
        if (userOptional.isEmpty()) {
            // Utilisateur non trouvé, retourne une réponse 404
            return ResponseEntity.notFound().build();
        }
        DBUser user = userOptional.get();

        // Conversion de l'entité utilisateur en DTO
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );

        // Retourne les détails de l'utilisateur avec une réponse 200 OK
        return ResponseEntity.ok(userDTO);
    }
}
