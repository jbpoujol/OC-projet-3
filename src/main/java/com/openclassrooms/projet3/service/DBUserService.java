package com.openclassrooms.projet3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.repository.DBUserRepository;

@Service
public class DBUserService {

    @Autowired
    private final DBUserRepository dbUserRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public DBUserService(DBUserRepository dbUserRepository) {
        this.dbUserRepository = dbUserRepository;
    }

    public DBUser registerUser(String name, String email, String password) {
        DBUser newUser = new DBUser();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(bCryptPasswordEncoder.encode(password));

        return dbUserRepository.save(newUser);
    }

    public DBUser find(String email) {
        return dbUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public DBUser findUserById(Long id) {
        return dbUserRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

}
