package com.openclassrooms.projet3.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
@Schema(description = "User entity representing a user in the system")
public class DBUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the user", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Column(name = "name", nullable = false)
    @Schema(description = "Name of the user", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    @Schema(description = "Email address of the user", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Column(name = "password", nullable = false, length = 60)
    @Schema(description = "Encrypted password of the user", example = "encryptedPassword", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "Set of rental properties owned by the user")
    private Set<Rental> houses = new HashSet<>();

    @Column(name = "created_at")
    @Schema(description = "Date when the user account was created", example = "2023-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    @Schema(description = "Date when the user account was last updated", example = "2023-01-02", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

}
