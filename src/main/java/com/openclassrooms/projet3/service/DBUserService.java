package com.openclassrooms.projet3.service;

import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.repository.DBUserRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DBUserService {

    private final DBUserRepository dbUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public DBUserService(DBUserRepository dbUserRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.dbUserRepository = dbUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Registers a new user with the provided name, email, and password.
     * This method performs several validations before creating a new user:
     * - Validates that the provided email is in a valid format using {@link EmailValidator}.
     * - Ensures the password is at least 8 characters long and contains a mix of upper and lower case letters, numbers, and special characters.
     * - Checks that the name is not null or empty.
     * If any of these validations fail, an {@link IllegalArgumentException} is thrown with an appropriate message.
     * Additionally, it checks if the email is already in use by another user. If so, an {@link IllegalStateException} is thrown,
     * indicating the email is already registered.
     * Upon passing all validations, the user's password is encrypted using {@link BCryptPasswordEncoder}, and
     * the new user is saved to the database.
     *
     * @param name the name of the user to be registered; must not be null or empty.
     * @param email the email of the user to be registered; must be in a valid format and not already in use.
     * @param password the password of the user to be registered; must meet the specified security criteria.
     * @return the registered {@link DBUser} object, including the generated ID and encrypted password.
     * @throws IllegalArgumentException if the email format is invalid, the password does not meet security criteria,
     *         or the name is null/empty.
     * @throws IllegalStateException if the email is already in use.
     */
    public DBUser registerUser(String name, String email, String password) {
        // Validation de l'email
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Validation du mot de passe
        if (password == null || password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*[0-9].*") || !password.matches(".*[!@#$%^&*()].*")) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and include a mix of upper and lower case letters, numbers, and special characters (!@#$%^&*())");
        }

        // Validation du nom
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        // Vérification de l'unicité de l'email
        if (dbUserRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Email already in use");
        }

        DBUser newUser = new DBUser();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(bCryptPasswordEncoder.encode(password));

        return dbUserRepository.save(newUser);
    }

    public Optional<DBUser> find(String email) {
        return dbUserRepository.findByEmail(email);
    }

    public Optional<DBUser> findUserById(Long id) {
        return dbUserRepository.findById(id);
    }

}
