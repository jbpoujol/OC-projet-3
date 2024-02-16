package com.openclassrooms.projet3.service;

import com.openclassrooms.projet3.model.DBUser;
import org.springframework.security.core.Authentication;

public interface JwtService {
    public String generateToken(Authentication authentication);

    public String generateTokenForUser(DBUser user);
}
