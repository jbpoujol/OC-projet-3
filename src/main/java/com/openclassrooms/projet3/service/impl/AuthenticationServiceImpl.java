package com.openclassrooms.projet3.service.impl;

import com.openclassrooms.projet3.dtos.UserDTO;
import com.openclassrooms.projet3.excepton.CustomAlreadyExistsException;
import com.openclassrooms.projet3.excepton.CustomAuthenticationException;
import com.openclassrooms.projet3.excepton.CustomNotFoundException;
import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.model.LoginRequest;
import com.openclassrooms.projet3.model.RegistrationRequest;
import com.openclassrooms.projet3.repository.DBUserRepository;
import com.openclassrooms.projet3.service.AuthenticationService;
import com.openclassrooms.projet3.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final DBUserRepository dbUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationServiceImpl(DBUserRepository dbUserRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.dbUserRepository = dbUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Map<String, String> registerUserAndGenerateToken(RegistrationRequest registrationRequest) {
        if (dbUserRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new CustomAlreadyExistsException("Email already in use.");
        }

        DBUser newUser = new DBUser();
        newUser.setName(registrationRequest.getName());
        newUser.setEmail(registrationRequest.getEmail());
        newUser.setPassword(bCryptPasswordEncoder.encode(registrationRequest.getPassword()));
        dbUserRepository.save(newUser);

        // Generate token for the new user
        String token = jwtService.generateTokenForUser(newUser);
        return Map.of("token", token);
    }

    @Override
    public Map<String, String> authenticateAndGenerateToken(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtService.generateToken(authentication);
            return Collections.singletonMap("token", jwt);
        } catch (AuthenticationException e) {
            throw new CustomAuthenticationException("Invalid username or password");
        }
    }

    @Override
    public String getAuthenticatedUserEmail() {
        Authentication authentication = getAuthentication();
        return (authentication != null) ? authentication.getName() : null;
    }

    @Override
    public String getAuthenticatedUsername() {
        Authentication authentication = getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
            return principal.toString();
        }
        return null;
    }

    @Override
    public UserDTO getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return dbUserRepository.findByEmail(email)
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()))
                .orElseThrow(() -> new CustomNotFoundException("User not found"));
    }


    /**
     * Fetches the current Authentication object from the SecurityContext.
     * <p>
     * This method is separated from {@link #getAuthenticatedUserEmail()} to facilitate unit testing.
     *
     * @return the current Authentication object, or {@code null} if no authentication information is available.
     */
    protected Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


}
