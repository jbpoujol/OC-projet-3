package com.openclassrooms.projet3.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.projet3.dtos.RentalDTO;
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
    public List<RentalDTO> getRentals() {
        Iterable<Rental> rentalsIterable = rentalService.findAllRentals();
        List<RentalDTO> rentalsList = StreamSupport.stream(rentalsIterable.spliterator(), false)
                .map(rental -> rentalService.convertToDTO(rental))
                .collect(Collectors.toList());
        return rentalsList;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRentalById(@PathVariable Long id) {
        return rentalService.findRentalById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createRental(@RequestBody Rental rental) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        DBUser owner = dbUserService.find(username);

        rental.setOwner(owner);
        rentalService.saveRental(rental);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Rental created!");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRental(@PathVariable Long id, @RequestBody Rental rentalDetails) {
        try {
            Rental existingRental = rentalService.findRentalById(id)
                    .orElseThrow(
                            () -> rentalService.new ResourceNotFoundException("Rental not found for this id :: " + id));

            if (rentalDetails.getName() != null)
                existingRental.setName(rentalDetails.getName());
            if (rentalDetails.getSurface() != 0)
                existingRental.setSurface(rentalDetails.getSurface());
            if (rentalDetails.getPrice() != 0.0)
                existingRental.setPrice(rentalDetails.getPrice());
            if (rentalDetails.getPicture() != null)
                existingRental.setPicture(rentalDetails.getPicture());
            if (rentalDetails.getDescription() != null)
                existingRental.setDescription(rentalDetails.getDescription());

            rentalService.saveRental(existingRental);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Rental updated!");
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Rental not found for this id :: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}
