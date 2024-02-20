package com.openclassrooms.projet3.service;

public interface AuthenticationService {

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
