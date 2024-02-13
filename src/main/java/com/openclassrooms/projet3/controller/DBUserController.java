package com.openclassrooms.projet3.controller;

import com.openclassrooms.projet3.dtos.UserDTO;
import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.service.DBUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class DBUserController {
    @Autowired
    private DBUserService dbUserService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        DBUser user = dbUserService.findUserById(id);
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
        return ResponseEntity.ok(userDTO);
    }
}
