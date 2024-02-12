package com.openclassrooms.projet3.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openclassrooms.projet3.model.DBUser;

public interface DBUserRepository extends JpaRepository<DBUser, Long> {

    public Optional<DBUser> findByName(String username);

    public Optional<DBUser> findByEmail(String email);

}
