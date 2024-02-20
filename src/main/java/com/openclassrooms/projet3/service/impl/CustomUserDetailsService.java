package com.openclassrooms.projet3.service.impl;

import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.repository.DBUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Custom service for managing user details.
 * Implements UserDetailsService to integrate user retrieval into Spring Security.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final DBUserRepository dbUserRepository;

    public CustomUserDetailsService(DBUserRepository dbUserRepository) {
        this.dbUserRepository = dbUserRepository;
    }

    /**
     * Loads a user by their email and constructs a UserDetails for authentication.
     * Although the method is named loadUserByUsername, it is adapted here to use email as the primary identifier.
     *
     * @param email The email of the user to load.
     * @return UserDetails containing the user's information for authentication.
     * @throws UsernameNotFoundException if no user is found with the provided email.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Searching for the user by email. Replace "findByName" with "findByEmail" or similar according to your implementation.
        DBUser user = dbUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));

        // Creating the list of authorities/grants. Here, a simple "USER" authority is assigned.
        // Adapt this part according to the roles and authorities in your application.
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("USER"));

        // Returns a Spring Security User object containing the necessary details for authentication.
        // Note that the email is used as "username" in this object.
        return new User(user.getEmail(), user.getPassword(), authorities);
    }
}