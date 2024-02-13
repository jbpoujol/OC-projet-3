package com.openclassrooms.projet3.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.openclassrooms.projet3.dtos.RentalDTO;
import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.model.Rental;
import com.openclassrooms.projet3.service.DBUserService;
import com.openclassrooms.projet3.service.RentalService;
import com.openclassrooms.projet3.service.RentalService.ResourceNotFoundException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * Handles the POST request to create a new rental.
     * This method accepts rental information as multipart/form-data, allowing for both text fields
     * and a file upload within a single request. It extracts rental properties from the request parameters,
     * stores the uploaded picture file on the disk, and saves the rental information along with the picture URL
     * in the database.
     * The method performs the following steps:
     * 1. Extracts user details from the security context to identify the rental owner.
     * 2. Saves the uploaded picture to the disk and generates a URL for accessing the picture.
     * 3. Creates a new Rental object with the provided details and the generated picture URL.
     * 4. Persists the new Rental object to the database.
     * 5. Returns a response entity with a success message if the rental is created successfully.
     * If any step fails, the method catches the exception and returns an internal server error response
     * indicating that the rental could not be created.
     *
     * @param name the name of the rental property
     * @param surface the surface area of the rental property
     * @param price the price of the rental property
     * @param description a description of the rental property
     * @param picture the picture file of the rental property
     * @return a ResponseEntity containing a success message and a 201 Created status code if successful,
     *         or an error message and a 500 Internal Server Error status code if an exception occurs.
     */
    @PostMapping()
    public ResponseEntity<?> createRental(@RequestParam("name") String name,
                                          @RequestParam("surface") int surface,
                                          @RequestParam("price") double price,
                                          @RequestParam("description") String description,
                                          @RequestParam("picture") MultipartFile picture) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            DBUser owner = dbUserService.find(email);

            String pictureUrl = storePicture(picture);

            Rental rental = new Rental();
            rental.setName(name);
            rental.setSurface(surface);
            rental.setPrice(price);
            rental.setDescription(description);
            rental.setPicture(pictureUrl);
            rental.setOwner(owner);

            rentalService.saveRental(rental);

            // Just return the message with HTTP status 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Rental created!"));
        } catch (Exception e) {
            // Handle exception (e.g., IOException from file storage or any other exception)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Could not create the rental"
            ));
        }
    }

    /**
     * Stores the uploaded picture file on the disk.
     * This method performs several checks and operations to securely save the uploaded file:
     * 1. It checks if the uploaded file is not empty, throwing an IOException if it is.
     * 2. It defines a directory path where the files should be stored and checks if this directory exists.
     *    If the directory does not exist, it creates it.
     * 3. It generates a unique filename for the uploaded file to prevent overwriting existing files.
     *    This is done by prefixing the original filename with the current system time in milliseconds.
     * 4. It resolves the path where the file should be stored and normalizes it to ensure it's a valid path.
     * 5. It checks to ensure the file is being stored within the predefined directory to prevent directory traversal attacks.
     * 6. It transfers the file to the resolved destination path.
     * 7. It returns the absolute path of the stored file as a string.
     *
     * @param file the MultipartFile object representing the uploaded picture.
     * @return the absolute path to the stored file as a String.
     * @throws IOException if the file is empty, if there's an issue creating the directories,
     *         if the file cannot be stored outside the predefined directory, or if there's an error during file transfer.
     */
    private String storePicture(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }
        String uploadsDirPath = "uploads";
        Path uploadsDir = Paths.get(uploadsDirPath);
        if (!Files.exists(uploadsDir)) {
            Files.createDirectories(uploadsDir);
        }
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path destinationFile = uploadsDir.resolve(filename).normalize().toAbsolutePath();

        if (!destinationFile.getParent().equals(uploadsDir.toAbsolutePath())) {
            throw new IOException("Cannot store file outside of the predefined directory.");
        }
        file.transferTo(destinationFile);
        return destinationFile.toString();
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
