package com.openclassrooms.projet3.config;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.repository.DBUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private DBUserRepository dbUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DBUser user = dbUserRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // Exemple avec des autorités hardcodées, ajustez selon votre modèle
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(user.getName(), user.getPassword(), authorities);
    }
}
