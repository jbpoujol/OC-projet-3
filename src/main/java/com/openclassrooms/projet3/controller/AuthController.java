package com.openclassrooms.projet3.controller;

import java.util.Collections;
import java.util.Map;

import com.openclassrooms.projet3.dtos.UserDTO;
import com.openclassrooms.projet3.repository.DBUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.model.LoginRequest;
import com.openclassrooms.projet3.model.RegistrationRequest;
import com.openclassrooms.projet3.service.DBUserService;
import com.openclassrooms.projet3.service.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private DBUserService dbUserService; // Service for user-related database operations

    @Autowired
    private JwtService jwtService; // Service for JWT token generation

    @Autowired
    private AuthenticationManager authenticationManager; // Manager for handling authentication process

    @Autowired
    private DBUserRepository userRepository; // Repository for user data

    /**
     * Endpoint for user registration.
     * Takes a RegistrationRequest object containing user details and registers a new user.
     *
     * @param registrationRequest the registration request containing user details
     * @return ResponseEntity with empty body
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                                      {}
                                                      """))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                                      {
                                                          "error": "Could not register the user. Please try again later."
                                                      }
                                                      """)))
            })
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        DBUser user = dbUserService.registerUser(
                registrationRequest.getName(),
                registrationRequest.getEmail(),
                registrationRequest.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.emptyMap()); // Return an empty object in the response body
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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication); // Update security context with authentication token

        String jwt = jwtService.generateToken(authentication); // Generate JWT token for the authenticated user
        return ResponseEntity.ok(Collections.singletonMap("token", jwt));
    }

    /**
     * Endpoint to retrieve current authenticated user's details.
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Assuming the username is the email for authenticated user

        DBUser user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        UserDTO response = new UserDTO(user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt());

        return ResponseEntity.ok(response);
    }

}
