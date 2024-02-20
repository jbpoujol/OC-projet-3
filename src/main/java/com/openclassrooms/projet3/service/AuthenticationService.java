package com.openclassrooms.projet3.service;

import com.openclassrooms.projet3.dtos.UserDTO;
import com.openclassrooms.projet3.excepton.CustomAlreadyExistsException;
import com.openclassrooms.projet3.excepton.CustomAuthenticationException;
import com.openclassrooms.projet3.model.LoginRequest;
import com.openclassrooms.projet3.model.RegistrationRequest;

import java.util.Map;

public interface AuthenticationService {

    /**
     * Registers a new user based on the provided registration request and generates a JWT token.
     * <p>
     * This method handles the registration of a new user by taking the user's details from the
     * {@link RegistrationRequest} object. It validates the request, creates a new user in the database,
     * and then generates a JWT token for the newly created user.
     *
     * @param registrationRequest the registration request containing details such as the user's name,
     *                            email, and password.
     * @return A {@link Map} containing the JWT token for the newly registered user. The token is
     * returned as a value associated with the "token" key.
     * @throws CustomAlreadyExistsException if the email provided in the registration request is already
     *                                      in use by another user.
     * @throws RuntimeException             if an unexpected error occurs during the registration or token generation
     *                                      process.
     */
    Map<String, String> registerUserAndGenerateToken(RegistrationRequest registrationRequest);

    /**
     * Authenticates a user based on the provided login request and generates a JWT token.
     * <p>
     * This method attempts to authenticate a user using the credentials provided in the
     * {@link LoginRequest} object. Upon successful authentication, it generates a JWT token
     * for the authenticated user.
     *
     * @param loginRequest the login request containing the user's credentials, including email
     *                     and password.
     * @return A {@link Map} containing the JWT token for the authenticated user. The token is
     * returned as a value associated with the "token" key.
     * @throws CustomAuthenticationException if the authentication process fails due to invalid
     *                                       credentials.
     * @throws RuntimeException              if an unexpected error occurs during the authentication or token
     *                                       generation process.
     */
    Map<String, String> authenticateAndGenerateToken(LoginRequest loginRequest);

    UserDTO getCurrentUserDetails();

    /**
     * Retrieves the email of the currently authenticated user.
     * <p>
     * This method is intended to be used in contexts where the user's email address is needed
     * and the user is expected to be authenticated. If no authentication information is available,
     * this method returns {@code null}.
     *
     * @return The email of the authenticated user if available, otherwise {@code null}.
     */
    String getAuthenticatedUserEmail();

    /**
     * Retrieves the username of the currently authenticated user.
     * <p>
     * This method obtains the username from the authentication principal. If the principal
     * is an instance of UserDetails, the username is directly extracted. Otherwise, the principal's
     * {@code toString()} representation is used as the username. This method returns {@code null}
     * if no authentication information is available.
     *
     * @return The username of the authenticated user if available, otherwise {@code null}.
     */
    String getAuthenticatedUsername();
}
