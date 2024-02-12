package com.openclassrooms.projet3.dtos;

import lombok.Data;

@Data
public class RentalDTO {
    private Long id;
    private String name;
    private int surface;
    private double price;
    private String picture;
    private String description;
    private Long ownerId;
    private String createdAt;
    private String updatedAt;
}
