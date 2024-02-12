package com.openclassrooms.projet3.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.model.Rental;
import com.openclassrooms.projet3.service.DBUserService;
import com.openclassrooms.projet3.service.RentalService;
import com.openclassrooms.projet3.service.RentalService.ResourceNotFoundException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;
    private final DBUserService dbUserService;

    public RentalController(RentalService rentalService, DBUserService dbUserService) {
        this.rentalService = rentalService;
        this.dbUserService = dbUserService;
    }

    @GetMapping
    public Iterable<Rental> getRentals() {
        return rentalService.findAllRentals();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRentalById(@PathVariable Long id) {
        return rentalService.findRentalById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Rental> createRental(@RequestBody Rental rental) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Obtenez le nom d'utilisateur de l'utilisateur authentifié
        DBUser owner = dbUserService.find(username); // Récupérez l'utilisateur de la base de données

        rental.setOwner(owner); // Affectez cet utilisateur comme propriétaire du rental
        Rental savedRental = rentalService.saveRental(rental);
        return ResponseEntity.ok(savedRental); // Retournez le rental sauvegardé
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rental> updateRental(@PathVariable Long id, @RequestBody Rental rentalDetails) {
        try {
            Rental updatedRental = rentalService.updateRental(id, rentalDetails);
            return ResponseEntity.ok(updatedRental);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
