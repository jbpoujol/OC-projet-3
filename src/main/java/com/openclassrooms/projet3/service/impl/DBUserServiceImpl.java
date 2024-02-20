package com.openclassrooms.projet3.service.impl;

import com.openclassrooms.projet3.dtos.UserDTO;
import com.openclassrooms.projet3.excepton.CustomNotFoundException;
import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.repository.DBUserRepository;
import com.openclassrooms.projet3.service.DBUserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DBUserServiceImpl implements DBUserService {

    private final DBUserRepository dbUserRepository;

    public DBUserServiceImpl(DBUserRepository dbUserRepository) {
        this.dbUserRepository = dbUserRepository;
    }

    @Override
    public Optional<DBUser> find(String email) {
        return dbUserRepository.findByEmail(email);
    }

    @Override
    public Optional<DBUser> findUserById(Long id) {
        return dbUserRepository.findById(id);
    }

    @Override
    public UserDTO findUserDTOById(Long id) {
        return dbUserRepository.findById(id)
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()))
                .orElseThrow(() -> new CustomNotFoundException("User not found"));
    }
}
