package com.openclassrooms.projet3.controller;

import com.openclassrooms.projet3.model.User;
import com.openclassrooms.projet3.service.UserService;

public class UserController {
    
        private final UserService userService;
    
        public UserController(UserService userService) {
            this.userService = userService;
        }
    
        public User saveUser(User user) {
            return userService.saveUser(user);
        }
}
