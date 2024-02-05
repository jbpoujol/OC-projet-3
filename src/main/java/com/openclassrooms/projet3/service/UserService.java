package com.openclassrooms.projet3.service;

import com.openclassrooms.projet3.model.User;
import com.openclassrooms.projet3.repository.UserRepository;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
}
