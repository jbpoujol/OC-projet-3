package com.openclassrooms.projet3.service.impl;

import com.openclassrooms.projet3.service.DBUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.repository.DBUserRepository;

import java.util.Optional;

@Service
public class DBUserServiceImpl implements DBUserService {

    @Autowired
    private final DBUserRepository dbUserRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public DBUserServiceImpl(DBUserRepository dbUserRepository) {
        this.dbUserRepository = dbUserRepository;
    }

    public DBUser registerUser(String name, String email, String password) {
        DBUser newUser = new DBUser();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(bCryptPasswordEncoder.encode(password));

        return dbUserRepository.save(newUser);
    }

    public Optional<DBUser> find(String email) {
        return dbUserRepository.findByEmail(email);
    }

    public Optional<DBUser> findUserById(Long id) {
        return dbUserRepository.findById(id);
    }
}
