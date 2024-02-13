package com.openclassrooms.projet3.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * Endpoint for user registration.
     * Takes a RegistrationRequest object containing user details and registers a new user.
     *
     * @param registrationRequest the registration request containing user details
     * @return ResponseEntity containing the registered DBUser object
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        DBUser user = dbUserService.registerUser(
                registrationRequest.getName(),
                registrationRequest.getEmail(),
                registrationRequest.getPassword());
        return ResponseEntity.ok(user);
    }

    /**
     * Endpoint for user login.
     * Authenticates the user with provided credentials and generates a JWT token upon successful authentication.
     *
     * @param loginRequest the login request containing user credentials
     * @return ResponseEntity with a map containing the JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getName(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication); // Update security context with authentication token

        String jwt = jwtService.generateToken(authentication); // Generate JWT token for the authenticated user
        return ResponseEntity.ok(Collections.singletonMap("token", jwt));
    }

}
