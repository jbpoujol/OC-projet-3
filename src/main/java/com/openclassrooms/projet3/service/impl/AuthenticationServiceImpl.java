package com.openclassrooms.projet3.service.impl;

import com.openclassrooms.projet3.service.AuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public String getAuthenticatedUserEmail() {
        Authentication authentication = getAuthentication();
        return (authentication != null) ? authentication.getName() : null;
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
