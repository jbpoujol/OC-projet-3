package com.openclassrooms.projet3.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.openclassrooms.projet3.excepton.CustomNotFoundException;
import com.openclassrooms.projet3.service.AuthenticationService;
import com.openclassrooms.projet3.utils.ImageUtils;
import com.openclassrooms.projet3.utils.impl.ImageUtilsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.openclassrooms.projet3.dtos.RentalDTO;
import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.model.Rental;
import com.openclassrooms.projet3.service.DBUserService;
import com.openclassrooms.projet3.service.impl.RentalServiceImpl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalServiceImpl rentalService;
    private final DBUserService dbUserService;
    private final ImageUtils imageUtils;
    private final AuthenticationService authenticationService;

    public RentalController(RentalServiceImpl rentalService, DBUserService dbUserService, ImageUtilsImpl imageUtils, AuthenticationService authenticationService) {
        this.rentalService = rentalService;
        this.dbUserService = dbUserService;
        this.imageUtils = imageUtils;
        this.authenticationService = authenticationService;
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
    @Operation(summary = "Get all rentals",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of rental list",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                               {
                                   "rentals": [
                                       {
                                           "id": 1,
                                           "name": "Charming Cottage",
                                           "surface": 120,
                                           "price": 1500.00,
                                           "picture": "http://example.com/images/cottage.jpg",
                                           "description": "A charming cottage in the countryside, perfect for a weekend getaway.",
                                           "owner_id": 42,
                                           "created_at": "2023-01-15T14:30:00Z",
                                           "updated_at": "2023-02-01T10:15:00Z"
                                       },
                                       {
                                           "id": 2,
                                           "name": "Urban Loft",
                                           "surface": 85,
                                           "price": 2100.00,
                                           "picture": "http://example.com/images/loft.jpg",
                                           "description": "Stylish loft in the heart of the city, close to amenities and nightlife.",
                                           "owner_id": 85,
                                           "created_at": "2023-01-20T11:00:00Z",
                                           "updated_at": "2023-01-28T09:20:00Z"
                                       }
                                   ]
                               }
                               """)))
            })
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
     * Retrieves a rental by its ID and returns it as a DTO.
     * This method handles a GET request to fetch a single rental identified by its unique ID. It utilizes
     * the rentalService to find the rental entity. If the rental is found, it is converted to a RentalDTO
     * using the rentalService's convertToDTO method. This conversion abstracts away the entity-to-DTO
     * transformation logic into the service layer, promoting a clean separation of concerns.
     * The method follows these steps:
     * 1. Calls the rentalService's findRentalById method with the provided ID to attempt to find the rental.
     * 2. If a rental is found, it uses a method reference (rentalService::convertToDTO) to convert the rental entity
     *    to a RentalDTO, ensuring that only the necessary data is exposed to the client.
     * 3. The RentalDTO is then wrapped in a ResponseEntity and returned with an HTTP status of 200 OK.
     * 4. If no rental is found for the provided ID, a ResponseEntity with an HTTP status of 404 Not Found is returned.
     * This approach ensures that the API's response structure and the underlying domain model can evolve
     * independently, providing flexibility and a stable contract to API consumers.
     *
     * @param id The ID of the rental to retrieve.
     * @return A ResponseEntity containing the RentalDTO if the rental is found, or a 404 Not Found status if not.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a rental by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RentalDTO.class),
                                    examples = @ExampleObject(value = """
                           {
                               "id": 1,
                               "name": "Charming Cottage",
                               "surface": 120,
                               "price": 1500.00,
                               "picture": "http://example.com/images/cottage.jpg",
                               "description": "A charming cottage in the countryside, perfect for a weekend getaway.",
                               "owner_id": 42,
                               "created_at": "2023-01-15",
                               "updated_at": "2023-02-01"
                           }
                           """)))
            })
    public ResponseEntity<?> getRentalById(@PathVariable Long id) {
        return rentalService.findRentalById(id)
                .map(rentalService::convertToDTO) // Utilisez la méthode de référence ici
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new rental", operationId = "createRental",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Rental created successfully",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                               {
                                   "message": "Rental created successfully!"
                               }
                               """))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                               {
                                   "error": "Could not create the rental"
                               }
                               """)))

            })
    public ResponseEntity<?> createRental(@RequestParam @NotBlank(message = "Name cannot be blank") String name,
                                          @RequestParam @NotNull(message = "Surface cannot be null") @Positive(message = "Surface must be positive") int surface,
                                          @RequestParam @NotNull(message = "Price cannot be null") @Positive(message = "Price must be positive") double price,
                                          @RequestParam @NotBlank(message = "Description cannot be blank") String description,
                                          @RequestParam(name = "picture", required = true) MultipartFile picture) {
        try {
            String email = authenticationService.getAuthenticatedUserEmail();
            Rental rental = rentalService.createRental(name, surface, price, description, picture, email);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Rental created!"));
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Could not create the rental"));
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
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update an existing rental",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rental successfully updated",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                   {
                                       "message": "Rental updated!"
                                   }
                                   """))),
                    @ApiResponse(responseCode = "403", description = "User is not the owner of the rental",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                   {
                                       "message": "User is not the owner of the rental"
                                   }
                                   """))),
                    @ApiResponse(responseCode = "404", description = "Rental not found",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                   {
                                       "message": "Rental not found"
                                   }
                                   """))),
                    @ApiResponse(responseCode = "500", description = "Error updating rental",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                   {
                                       "message": "Error updating rental: [Error details here]"
                                   }
                                   """)))
            })
    public ResponseEntity<?> updateRental(@PathVariable Long id,
                                          @RequestParam @NotBlank(message = "Name cannot be empty") String name,
                                          @RequestParam @NotNull(message = "Surface cannot be null")
                                          @Min(value = 1, message = "Surface must be greater than 0") int surface,
                                          @RequestParam @NotNull(message = "Price cannot be null")
                                          @Positive(message = "Price must be positive") double price,
                                          @RequestParam @NotBlank(message = "Description cannot be empty") String description,
                                          @RequestParam(value = "picture", required = false) MultipartFile picture) {

        // TODO : Implement the updateRental method
        try {
            Rental rental = rentalService.findRentalById(id).orElseThrow(() -> new Exception("Rental not found"));

            rental.setName(name);
            rental.setSurface(surface);
            rental.setPrice(price);
            rental.setDescription(description);

            if (picture != null && !picture.isEmpty()) {
                String pictureUrl = imageUtils.storePicture(picture);
                rental.setPicture(pictureUrl);
            }

            // Checking if the user is the owner of the rental before updating it
            if (!rentalService.isUserOwnerOfRental(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "User is not the owner of the rental"));
            }

            return ResponseEntity.ok().body(Map.of("message", "Rental updated!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error updating rental: " + e.getMessage()));
        }
    }


}
