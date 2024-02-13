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

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

@Validated
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
     * This method fetches all rental entities using the rental service, converts each entity
     * to its Data Transfer Object (DTO) representation, and then collects them into a list.
     * The list of RentalDTO objects is then encapsulated within a Map under the key 'rentals',
     * allowing the response to be easily extended in the future with additional data if necessary.
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
     * Creates a new rental property with validated input data.
     * This method handles a POST request to create a new rental. It validates the input data to ensure
     * it adheres to specified constraints, such as non-blank values for name and description, and positive
     * values for surface area and price. If the validation fails, a 400 Bad Request response is automatically
     * returned with validation error messages. The picture file is not validated by annotations but is checked
     * for emptiness before processing.
     * The process involves:
     * 1. Validating the incoming request parameters against the defined constraints.
     * 2. Extracting the authenticated user's email from the security context to set as the rental's owner.
     * 3. Storing the uploaded picture on the disk and generating its URL for the rental entity.
     * 4. Creating and saving the new Rental entity with the provided and validated values.
     * 5. Returning a success response if the rental is created successfully.
     *
     * @param name The name of the rental, must not be blank.
     * @param surface The surface area of the rental, must be a positive integer.
     * @param price The price of the rental, must be a positive value.
     * @param description The description of the rental, must not be blank.
     * @param picture The picture file for the rental, must not be empty.
     * @return A ResponseEntity with a success message and a 201 Created status if successful,
     *         or an error message with a 400 Bad Request status if validation fails,
     *         or a 500 Internal Server Error status if an exception occurs during the creation process.
     */

    @PostMapping()
    public ResponseEntity<?> createRental(@RequestParam @NotBlank(message = "Name cannot be blank") String name,
                                          @RequestParam @NotNull(message = "Surface cannot be null") @Positive(message = "Surface must be positive") int surface,
                                          @RequestParam @NotNull(message = "Price cannot be null") @Positive(message = "Price must be positive") double price,
                                          @RequestParam @NotBlank(message = "Description cannot be blank") String description,
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
     * Updates an existing rental property by its ID with validated input data.
     * This method handles a PUT request to update a rental's details including its name, surface area, price,
     * description, and optionally, a new picture. The method validates the input data to ensure it meets specified
     * constraints, such as non-null values for certain fields and positive values for numeric fields. If the validation
     * fails, a 400 Bad Request response is automatically returned with validation error messages.
     * Steps involved in the process:
     * 1. Validates the incoming request parameters against the defined constraints.
     * 2. Retrieves the existing rental entity by its ID. If not found, throws an exception.
     * 3. Updates the rental entity's properties with the provided and validated values.
     * 4. If a new picture file is provided and is not empty, it replaces the existing picture. The file is stored
     *    on the disk, and its storage path or URL is saved in the rental entity.
     * 5. Persists the updated rental entity to the database.
     * 6. Returns a success response if the rental is updated successfully.
     * Input parameters are validated for:
     * - Name and description must not be blank.
     * - Surface must be a non-null value greater than 0.
     * - Price must be a non-null positive value.
     * If a new picture is uploaded, it is processed and stored, and its URL is updated in the rental entity.
     *
     * @param id The ID of the rental to update.
     * @param name The new name of the rental, must not be blank.
     * @param surface The new surface area of the rental, must be a positive integer.
     * @param price The new price of the rental, must be a positive value.
     * @param description The new description of the rental, must not be blank.
     * @param picture (Optional) A new picture file for the rental. If provided, it replaces the existing picture.
     * @return A ResponseEntity with a success message and a 200 OK status if successful,
     *         or an error message with a 400 Bad Request status if validation fails,
     *         or a 500 Internal Server Error status if an exception occurs during the update process.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRental(@PathVariable Long id,
                                          @RequestParam @NotBlank(message = "Name cannot be empty") String name,
                                          @RequestParam @NotNull(message = "Surface cannot be null")
                                          @Min(value = 1, message = "Surface must be greater than 0") int surface,
                                          @RequestParam @NotNull(message = "Price cannot be null")
                                          @Positive(message = "Price must be positive") double price,
                                          @RequestParam @NotBlank(message = "Description cannot be empty") String description,
                                          @RequestParam(value = "picture", required = false) MultipartFile picture) {
        try {
            Rental rental = rentalService.findRentalById(id).orElseThrow(() -> new Exception("Rental not found"));

            rental.setName(name);
            rental.setSurface(surface);
            rental.setPrice(price);
            rental.setDescription(description);

            if (picture != null && !picture.isEmpty()) {
                String pictureUrl = storePicture(picture);
                rental.setPicture(pictureUrl);
            }

            rentalService.saveRental(rental);

            return ResponseEntity.ok().body("Rental updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating rental: " + e.getMessage());
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

}
