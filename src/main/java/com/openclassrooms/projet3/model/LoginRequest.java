package com.openclassrooms.projet3.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;

}
