package com.openclassrooms.projet3.service.impl;

import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.repository.DBUserRepository;
import com.openclassrooms.projet3.service.DBUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DBUserServiceImpl implements DBUserService {

    private final DBUserRepository dbUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public DBUserServiceImpl(DBUserRepository dbUserRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.dbUserRepository = dbUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public DBUser registerUser(String name, String email, String password) {
        DBUser newUser = new DBUser();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(bCryptPasswordEncoder.encode(password));

        return dbUserRepository.save(newUser);
    }

    @Override
    public Optional<DBUser> find(String email) {
        return dbUserRepository.findByEmail(email);
    }

    @Override
    public Optional<DBUser> findUserById(Long id) {
        return dbUserRepository.findById(id);
    }
}
