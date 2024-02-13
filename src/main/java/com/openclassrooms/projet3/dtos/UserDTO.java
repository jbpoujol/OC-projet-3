package com.openclassrooms.projet3.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private LocalDate created_at;
    private LocalDate updated_at;

    public UserDTO(Long id, String name, String email, LocalDate created_at, LocalDate updated_at) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
}
