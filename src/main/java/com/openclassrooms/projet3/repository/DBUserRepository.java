package com.openclassrooms.projet3.repository;

import com.openclassrooms.projet3.model.DBUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DBUserRepository extends CrudRepository<DBUser, Long> {

    Optional<DBUser> findByName(String username);

    Optional<DBUser> findByEmail(String email);

    boolean existsByEmail(String email);
}
