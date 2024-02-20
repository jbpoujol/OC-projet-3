package com.openclassrooms.projet3.service.impl;

import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.service.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtServiceImpl implements JwtService {
    private final JwtEncoder jwtEncoder;

    public JwtServiceImpl(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public String generateToken(Authentication authentication) {
        return generateTokenWithClaims(authentication.getName());
    }

    @Override
    public String generateTokenForUser(DBUser user) {
        return generateTokenWithClaims(user.getEmail());
    }

    /**
     * Génère un token JWT pour un sujet donné.
     *
     * @param subject Le sujet pour lequel le token est généré (typiquement un nom d'utilisateur ou un email).
     * @return Le token JWT généré.
     */
    private String generateTokenWithClaims(String subject) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.DAYS))
                .subject(subject)
                .build();

        JwtEncoderParameters params = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return this.jwtEncoder.encode(params).getTokenValue();
    }
}