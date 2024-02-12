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
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private DBUserService dbUserService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        DBUser user = dbUserService.registerUser(
                registrationRequest.getName(),
                registrationRequest.getEmail(),
                registrationRequest.getPassword());
        return ResponseEntity.ok(user);
    }
    /*
     * @PostMapping("/login")
     * public String getToken(Authentication authentication) {
     * String token = jwtService.generateToken(authentication);
     * return token;
     * }
     */

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getName(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtService.generateToken(authentication);
        return ResponseEntity.ok(Collections.singletonMap("token", jwt));
    }

}
