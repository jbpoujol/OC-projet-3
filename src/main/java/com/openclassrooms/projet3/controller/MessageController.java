package com.openclassrooms.projet3.controller;

import com.openclassrooms.projet3.dtos.MessageDTO;
import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.model.Message;
import com.openclassrooms.projet3.model.Rental;
import com.openclassrooms.projet3.service.DBUserService;
import com.openclassrooms.projet3.service.MessageService;
import com.openclassrooms.projet3.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private RentalService rentalService;

    @Autowired
    private DBUserService dbUserService;

    /**
     * Creates and stores a message sent by a user to a rental property's owner.
     * This method handles a POST request to create a new message associated with a specific rental
     * property and user. It takes a validated MessageDTO object as input, which contains the rental ID,
     * user ID, and the message content. The method performs the following steps:
     * 1. Validates the incoming MessageDTO object to ensure it meets all defined constraints, such as non-null
     *    rental and user IDs, and a non-blank message. If validation fails, Spring automatically returns a
     *    400 Bad Request response detailing the validation errors.
     * 2. Attempts to find the rental property by its ID using the rentalService. If the specified rental
     *    property is not found, it throws a RuntimeException with a "Rental not found" message.
     * 3. Looks up the user by their ID using the dbUserService. Assumes the user exists without explicit
     *    null checking, which could throw a NullPointerException if a user with the given ID does not exist.
     *    It's recommended to handle potential null values or non-existent users more gracefully.
     * 4. Constructs a new Message entity with the found rental property, user, and the provided message content.
     *    Sets the current date and time as the creation and update timestamps.
     * 5. Persists the new message entity to the database using messageService.
     * 6. Returns a 200 OK response with a JSON object containing a success message if the message is saved successfully.
     * If any exception occurs during the process, for example, if the rental property is not found or there's
     * a database error, the method catches the exception and returns a 500 Internal Server Error response with
     * the exception's message.
     * Note: This method assumes the existence of services for handling database operations for rentals, users,
     * and messages. It does not explicitly handle cases where the user ID does not correspond to an existing user,
     * which could lead to potential errors.
     *
     * @param messageDTO The MessageDTO object containing the rental ID, user ID, and message content, validated
     *                   for non-null IDs and non-blank message content.
     * @return A ResponseEntity containing a success message in the body if the operation is successful, or
     *         an error message if an exception occurs.
     */
    @PostMapping
    @Operation(summary = "Create a new message",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Message sent successfully",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                               {
                                   "message": "Message sent with success"
                               }
                               """))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                               {
                                   "error": "Could not send the message. Please try again later."
                               }
                               """)))
            })
    public ResponseEntity<?> createMessage(@RequestBody @Valid MessageDTO messageDTO) {
        try {
            Rental rental = rentalService.findRentalById(messageDTO.getRental_id())
                    .orElseThrow(() -> new RuntimeException("Rental not found"));
            DBUser user = dbUserService.findUserById(messageDTO.getUser_id());

            Message message = new Message();
            message.setRental(rental);
            message.setUser(user);
            message.setMessage(messageDTO.getMessage());

            messageService.saveMessage(message);

            return ResponseEntity.ok(Map.of("message", "Message sent with success"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Could not send the message. Please try again later."));
        }
    }

}
