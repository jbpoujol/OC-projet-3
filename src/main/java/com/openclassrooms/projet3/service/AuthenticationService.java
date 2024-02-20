package com.openclassrooms.projet3.service;

public interface AuthenticationService {

    /**
     * Retrieves the email address of the currently authenticated user.
     * <p>
     * This method is designed to abstract the process of obtaining the email address
     * of the user currently authenticated in the security context of the application.
     * It assumes that the authentication object's principal or name represents the user's
     * email address.
     * <p>
     * It's important to note that the method will return null if there's no authentication
     * information available in the security context, indicating that the request is not
     * associated with any authenticated user.
     *
     * @return The email address of the currently authenticated user, or {@code null} if the user is not authenticated.
     */
    String getAuthenticatedUserEmail();
}
