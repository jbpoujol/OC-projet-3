package com.openclassrooms.projet3.controller;

import com.openclassrooms.projet3.dtos.RentalDTO;
import com.openclassrooms.projet3.excepton.CustomNotFoundException;
import com.openclassrooms.projet3.model.Rental;
import com.openclassrooms.projet3.service.AuthenticationService;
import com.openclassrooms.projet3.service.DBUserService;
import com.openclassrooms.projet3.service.impl.RentalServiceImpl;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalServiceImpl rentalService;
    private final ImageUtils imageUtils;
    private final AuthenticationService authenticationService;

    public RentalController(RentalServiceImpl rentalService, DBUserService dbUserService, ImageUtilsImpl imageUtils, AuthenticationService authenticationService) {
        this.rentalService = rentalService;
        this.imageUtils = imageUtils;
        this.authenticationService = authenticationService;
    }

    /**
     * Retrieves a list of all rental properties available in the system.
     * <p>
     * This endpoint fetches a comprehensive list of rental properties, including details such as name, surface area, price, description, and picture URL for each rental, intended for client display.
     * <p>
     * Responses:
     * - 200 OK: Successfully retrieved the list of rentals, returned as an array of rental objects within a "rentals" key in the response body.
     * - 500 Internal Server Error: Indicates an unexpected error occurred during the process. This could be due to database issues, file storage problems, or other internal server errors. The response includes an error message providing more details about the specific issue encountered.
     * <p>
     * The method leverages the {@code RentalService} to fetch and transform rental data into DTOs before returning to the caller. It demonstrates separation of concerns by encapsulating the business logic for fetching rental information, leaving the controller responsible for request handling and response formatting. Global exception handling is in place to catch and respond to any unhandled exceptions that may occur.
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
                                            """))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                                "error": "An unexpected error occurred",
                                                "details": "Specific error message here"
                                            }
                                            """)))
            })
    public Map<String, Object> getRentals() {
        return rentalService.getRentalsWithDTOs();
    }

    /**
     * Retrieves a specific rental by its ID and returns it as a DTO.
     * <p>This method processes a GET request to fetch a single rental, identified by its unique ID, from the database.
     * It utilizes the {@code RentalService} to locate the corresponding rental entity. Upon finding the rental,
     * it is transformed into a {@code RentalDTO}, which is then returned to the client. This method ensures that
     * the data transfer object contains only the necessary information that should be exposed to the client,
     * maintaining data privacy and minimizing the payload size.</p>
     *
     * <p><strong>Method Workflow:</strong></p>
     * <ol>
     *     <li>Validate the provided ID to ensure it meets the minimum value requirement.</li>
     *     <li>Invoke {@code RentalService.findRentalDTOById(id)} to attempt to find the rental.</li>
     *     <li>If a rental is found, return it as {@code RentalDTO} with a 200 OK status.</li>
     *     <li>If no rental is found, or if the provided ID is invalid, appropriate error responses are returned.</li>
     * </ol>
     *
     * <p><strong>Exception Handling:</strong></p>
     * <ul>
     *     <li>If the ID does not meet the validation criteria (e.g., a non-positive number), a
     *     {@code ConstraintViolationException} is thrown, resulting in a 400 Bad Request response with a
     *     validation error message.</li>
     *     <li>If no rental matches the provided ID, a {@code CustomNotFoundException} is thrown, leading to
     *     a 404 Not Found response.</li>
     * </ul>
     *
     * <p><strong>API Responses:</strong></p>
     * <ul>
     *     <li><em>200 OK:</em> Returns a {@code RentalDTO} containing the rental details.</li>
     *     <li><em>400 Bad Request:</em> Occurs when request parameters do not meet validation requirements.
     *     Example error message:
     *     <pre>{
     *         "error": "Validation error",
     *         "details": ["ID must be greater than 0"]
     *     }</pre></li>
     *     <li><em>404 Not Found:</em> Occurs when no rental is found for the provided ID.
     *     The response body is typically empty.</li>
     * </ul>
     *
     * @param id The ID of the rental to retrieve.
     * @return A {@link ResponseEntity} containing the {@link RentalDTO} if the rental is found, or an error response otherwise.
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
                                            """))),
                    @ApiResponse(responseCode = "400", description = "Validation error on request parameters",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                                "error": "Validation error",
                                                "details": ["ID must be greater than 0"]
                                            }
                                            """))),
                    @ApiResponse(responseCode = "404", description = "Rental not found for the provided ID",
                            content = @Content)
            })
    public ResponseEntity<?> getRentalById(@PathVariable @Min(1) Long id) {
        RentalDTO rentalDTO = rentalService.findRentalDTOById(id);
        return ResponseEntity.ok(rentalDTO);
    }

    /**
     * Handles the creation of a new rental listing.
     * This endpoint consumes multipart/form-data to allow for picture uploads alongside rental data.
     *
     * @param name        The name of the rental property, must not be blank.
     * @param surface     The surface area of the rental property in square meters, must be a positive integer.
     * @param price       The rental price, must be a positive number.
     * @param description A description of the rental property, must not be blank.
     * @param picture     A multipart file containing the picture of the rental property, required.
     * @return A ResponseEntity containing a success message with HTTP status 201 if the rental is created successfully,
     * a not found message with HTTP status 404 if the owner is not found,
     * or an error message with HTTP status 500 if an internal server error occurs during the creation process.
     * The method first retrieves the email of the currently authenticated user, which is assumed to be the owner of the rental.
     * It then attempts to create a new rental listing with the provided details and the owner's information.
     * If the owner is not found in the system, it responds with a 404 status code and an appropriate error message.
     * Any other exceptions that occur during the creation process result in a 500 internal server error response.
     */
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
                    @ApiResponse(responseCode = "404", description = "Owner not found",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                                "error": "Owner not found"
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
     * Updates an existing rental with the provided details.
     * <p>
     * This endpoint allows for updating the details of an existing rental identified by its ID. The operation
     * requires providing the rental's new name, surface area, price, description, and an optional picture.
     * It enforces several constraints to ensure valid data is submitted, including non-blank name and description,
     * a surface area and price that must be positive, and a valid rental ID.
     * <p>
     * The update process involves authenticating the user to verify ownership of the rental before proceeding
     * with the update. If the user is not the owner, a 403 Forbidden response is returned. If the rental
     * is not found, a 404 Not Found response is generated. Successful updates return a 200 OK with a message
     * indicating the success. Any server-side errors during the process result in a 500 Internal Server Error response.
     *
     * @param id          The ID of the rental to update, must be greater than 0.
     * @param name        The new name for the rental, cannot be blank.
     * @param surface     The new surface area for the rental, must be a positive integer.
     * @param price       The new price for the rental, must be a positive value.
     * @param description The new description for the rental, cannot be blank.
     * @param picture     An optional new picture file for the rental.
     * @return A ResponseEntity with a success message, or an error message.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update an existing rental",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rental successfully updated",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                                "message": "Rental updated successfully!"
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
    public ResponseEntity<?> updateRental(@PathVariable @Min(1) Long id,
                                          @RequestParam @NotBlank(message = "Name cannot be empty") String name,
                                          @RequestParam @NotNull(message = "Surface cannot be null") @Min(value = 1, message = "Surface must be greater than 0") int surface,
                                          @RequestParam @NotNull(message = "Price cannot be null") @Positive(message = "Price must be positive") double price,
                                          @RequestParam @NotBlank(message = "Description cannot be empty") String description,
                                          @RequestParam(value = "picture", required = false) MultipartFile picture) {
        try {
            String ownerEmail = authenticationService.getAuthenticatedUserEmail();
            Rental updatedRental = rentalService.updateRental(id, name, surface, price, description, picture, ownerEmail);

            return ResponseEntity.ok().body(Map.of("message", "Rental updated successfully!"));
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error updating rental: " + e.getMessage()));
        }
    }

}
