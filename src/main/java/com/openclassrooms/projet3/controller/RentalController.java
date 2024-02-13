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

    /**
     * Handles the GET request to retrieve all rentals.
     *
     * This method fetches all rental entities using the rental service, converts each entity
     * to its Data Transfer Object (DTO) representation, and then collects them into a list.
     * The list of RentalDTO objects is then encapsulated within a Map under the key 'rentals',
     * allowing the response to be easily extended in the future with additional data if necessary.
     *
     * The use of StreamSupport along with the spliterator of the Iterable allows for
     * efficient streaming and transformation of the rental entities to DTOs.
     *
     * @return A Map containing the list of RentalDTOs under the key 'rentals',
     *         which is then serialized into a JSON object response.
     */
    @GetMapping
    public Map<String, Object> getRentals() {
        Iterable<Rental> rentalsIterable = rentalService.findAllRentals();
        List<RentalDTO> rentalsList = StreamSupport.stream(rentalsIterable.spliterator(), false)
                .map(rentalService::convertToDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("rentals", rentalsList);
        return response;
    }

    /**
     * Handles the GET request to retrieve a single rental by its ID.
     * This method searches for a rental using the provided ID by calling the rental service.
     * If a rental with the specified ID is found, it is returned wrapped in a ResponseEntity with an HTTP status of OK (200).
     * If the rental cannot be found, a ResponseEntity with an HTTP status of NOT FOUND (404) is returned instead.
     * The use of Java 8's Optional.map() method allows for concise handling of the service's return value.
     * If a value is present (the rental is found), it is transformed into a ResponseEntity with the rental data.
     * If no value is present (the rental is not found), the orElseGet() method is used to supply a ResponseEntity
     * indicating that the resource was not found.
     *
     * @param id The ID of the rental to retrieve.
     * @return A ResponseEntity containing the rental if found, or a NOT FOUND status if not.
     */
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
