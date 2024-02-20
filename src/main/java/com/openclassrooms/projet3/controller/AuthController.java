package com.openclassrooms.projet3.controller;

import com.openclassrooms.projet3.dtos.UserDTO;
import com.openclassrooms.projet3.excepton.CustomAlreadyExistsException;
import com.openclassrooms.projet3.excepton.CustomAuthenticationException;
import com.openclassrooms.projet3.excepton.CustomNotFoundException;
import com.openclassrooms.projet3.model.LoginRequest;
import com.openclassrooms.projet3.model.RegistrationRequest;
import com.openclassrooms.projet3.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Endpoint for user registration.
     * Takes a RegistrationRequest object containing user details and registers a new user.
     * Upon successful registration, returns a JWT token for the user.
     *
     * @param registrationRequest the registration request containing user details
     * @return ResponseEntity with JWT token
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user and return a JWT token",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully, JWT token returned",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"token\": \"jwt\"}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"error\": \"Could not register the user. Please try again later.\"}")))
            })
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        try {
            Map<String, String> tokenResponse = authenticationService.registerUserAndGenerateToken(registrationRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse);
        } catch (CustomAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Could not register the user. Please try again later."));
        }
    }

    /**
     * Endpoint for user login.
     * Authenticates the user with provided credentials and generates a JWT token upon successful authentication.
     *
     * @param loginRequest the login request containing user credentials
     * @return ResponseEntity with a map containing the JWT token
     */
    @PostMapping("/login")
    @Operation(summary = "Login user and return JWT token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                                "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                            }
                                            """))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                                "error": "Invalid username or password"
                                            }
                                            """)))
            })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Map<String, String> tokenResponse = authenticationService.authenticateAndGenerateToken(loginRequest);
            return ResponseEntity.ok(tokenResponse);
        } catch (CustomAuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint to retrieve current authenticated user's details.
     *
     * @return ResponseEntity containing the user's details
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Current user details retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "id": 1,
                                                "name": "John Doe",
                                                "email": "john.doe@example.com",
                                                "createdAt": "2020-01-01",
                                                "updatedAt": "2020-01-02"
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
    public ResponseEntity<?> getCurrentUserDetails() {
        try {
            UserDTO userDTO = authenticationService.getCurrentUserDetails();
            return ResponseEntity.ok(userDTO);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

}
