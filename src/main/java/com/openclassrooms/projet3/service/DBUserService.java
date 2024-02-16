package com.openclassrooms.projet3.service;

import com.openclassrooms.projet3.model.DBUser;

import java.util.Optional;

public interface DBUserService {

    public DBUser registerUser(String name, String email, String password);

    public Optional<DBUser> find(String email);

    public Optional<DBUser> findUserById(Long id);
}
