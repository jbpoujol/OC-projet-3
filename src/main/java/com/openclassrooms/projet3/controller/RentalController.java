package com.openclassrooms.projet3.controller;

import com.openclassrooms.projet3.dtos.ApiStandardResponse;
import com.openclassrooms.projet3.dtos.RentalDTO;
import com.openclassrooms.projet3.dtos.RentalListResponse;
import com.openclassrooms.projet3.excepton.CustomNotFoundException;
import com.openclassrooms.projet3.model.Rental;
import com.openclassrooms.projet3.service.AuthenticationService;
import com.openclassrooms.projet3.service.RentalService;
import com.openclassrooms.projet3.service.impl.RentalServiceImpl;
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

import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;
    private final AuthenticationService authenticationService;

    public RentalController(RentalServiceImpl rentalService, AuthenticationService authenticationService) {
        this.rentalService = rentalService;
        this.authenticationService = authenticationService;
    }

    /**
     * Fetches and returns a list of all rental properties available.
     * <p>
     * This endpoint provides a comprehensive list of all rental properties, including their names, surface areas,
     * prices, descriptions, and picture URLs. It's designed to support client-side display needs by offering a detailed
     * overview of each property available for rent.
     * <p>
     * The method queries the underlying rental service to retrieve a collection of {@link RentalDTO}s, which are then
     * returned to the caller wrapped inside a response entity with an HTTP status code of 200 for a successful operation.
     * In the event of an unexpected server error, a 500 status code is returned along with an error message detailing
     * the nature of the error.
     *
     * @return A {@link ResponseEntity} containing a {@link Map} with a key "rentals" mapped to a list of {@link RentalDTO}s.
     * The response entity will have an HTTP status code of 200 (OK) on success or 500 (Internal Server Error) if
     * an unexpected error occurs.
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
    public ResponseEntity<RentalListResponse> getRentals() {
        List<RentalDTO> rentals = rentalService.getRentalsWithDTOs();
        RentalListResponse response = new RentalListResponse(rentals);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves detailed information for a specific rental property identified by its ID.
     * <p>
     * This endpoint is designed to fetch detailed information about a single rental property, including its name,
     * surface area, price, description, and picture URL, based on the provided unique identifier (ID). The information
     * is returned as a {@link RentalDTO} object, encapsulating the rental details in a format suitable for client-side consumption.
     * <p>
     * <strong>Path Variable:</strong>
     * <ul>
     *     <li>{@code id}: The unique identifier of the rental property to retrieve. Must be greater than 0.</li>
     * </ul>
     *
     * <strong>Responses:</strong>
     * <ul>
     *     <li><em>200 OK:</em> Successful retrieval of the rental information. The response body contains a {@link RentalDTO}
     *     object with the rental details.</li>
     *     <li><em>400 Bad Request:</em> The provided ID does not meet the validation criteria (e.g., a non-positive number).
     *     The response includes a validation error message.</li>
     *     <li><em>404 Not Found:</em> No rental property was found for the provided ID. The response body typically does not
     *     contain any additional information.</li>
     * </ul>
     *
     * @param id The ID of the rental property to retrieve, encapsulated as a {@code @PathVariable}. This ID must be a positive number.
     * @return A {@link ResponseEntity} containing the {@link RentalDTO} of the requested rental property if found, or an appropriate
     * error response otherwise.
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
    public ResponseEntity<RentalDTO> getRentalById(@PathVariable @Min(1) Long id) {
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
    public ResponseEntity<ApiStandardResponse> createRental(@RequestParam @NotBlank String name,
                                                            @RequestParam @NotNull @Positive int surface,
                                                            @RequestParam @NotNull @Positive double price,
                                                            @RequestParam @NotBlank String description,
                                                            @RequestParam("picture") MultipartFile picture) {
        try {
            String email = authenticationService.getAuthenticatedUserEmail();
            Rental rental = rentalService.createRental(name, surface, price, description, picture, email);
            ApiStandardResponse response = new ApiStandardResponse(true, "Rental created successfully!", Map.of("rentalId", rental.getId()));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (CustomNotFoundException e) {
            ApiStandardResponse errorResponse = new ApiStandardResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ApiStandardResponse errorResponse = new ApiStandardResponse("Could not create the rental");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
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
    public ResponseEntity<ApiStandardResponse> updateRental(@PathVariable @Min(1) Long id,
                                                            @RequestParam @NotBlank String name,
                                                            @RequestParam @NotNull @Positive int surface,
                                                            @RequestParam @NotNull @Positive double price,
                                                            @RequestParam @NotBlank String description,
                                                            @RequestParam(value = "picture", required = false) MultipartFile picture) {
        try {
            String ownerEmail = authenticationService.getAuthenticatedUserEmail();
            Rental updatedRental = rentalService.updateRental(id, name, surface, price, description, picture, ownerEmail);
            return ResponseEntity.ok(new ApiStandardResponse(true, "Rental updated successfully!"));
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiStandardResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiStandardResponse("Error updating rental: " + e.getMessage()));
        }
    }


}
