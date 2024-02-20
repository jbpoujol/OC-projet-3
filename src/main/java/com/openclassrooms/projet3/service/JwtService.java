package com.openclassrooms.projet3.service;

import com.openclassrooms.projet3.model.DBUser;
import org.springframework.security.core.Authentication;

public interface JwtService {

    /**
     * Generates a JWT token for a given authentication.
     * <p>
     * This method utilizes the provided Authentication object to create a JWT token
     * that represents the authenticated user's identity, including their authorities
     * and principal information.
     *
     * @param authentication the Authentication object containing the user's authentication information.
     * @return a JWT token as a String.
     */
    String generateToken(Authentication authentication);

    /**
     * Generates a JWT token for a specific user.
     * <p>
     * This method creates a JWT token for the given user, incorporating essential
     * user details into the token's claims. This token can be used to authenticate
     * the user in subsequent requests.
     *
     * @param user the DBUser object representing the user for whom the token is to be generated.
     * @return a JWT token as a String, specific to the given user.
     */
    String generateTokenForUser(DBUser user);
}
