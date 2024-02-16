package com.openclassrooms.projet3.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openclassrooms.projet3.model.DBUser;
import org.springframework.data.repository.CrudRepository;

public interface DBUserRepository extends CrudRepository<DBUser, Long> {

    public Optional<DBUser> findByName(String username);

    public Optional<DBUser> findByEmail(String email);

}
