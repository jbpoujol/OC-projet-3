package com.openclassrooms.projet3.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.projet3.model.Rental;
import com.openclassrooms.projet3.service.RentalService;
import com.openclassrooms.projet3.service.RentalService.ResourceNotFoundException;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    
    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
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
    public Rental createRental(@RequestBody Rental rental) {
        System.out.println(rental.getName());
        return rentalService.saveRental(rental);
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
