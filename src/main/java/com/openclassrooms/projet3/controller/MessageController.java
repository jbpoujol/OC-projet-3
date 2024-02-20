package com.openclassrooms.projet3.controller;

import com.openclassrooms.projet3.dtos.MessageDTO;
import com.openclassrooms.projet3.excepton.CustomNotFoundException;
import com.openclassrooms.projet3.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Creates a new message associated with a specific rental and user.
     * <p>
     * This endpoint receives message details as a request body in the form of a MessageDTO, which includes
     * the rental ID, user ID, and the message content. The service layer is responsible for validating the
     * existence of both the rental and the user, creating a new message, and saving it to the database.
     * <p>
     * Responses:
     * <ul>
     *     <li><b>200 OK:</b> The message was successfully created and saved. The response includes a success
     *     message.</li>
     *     <li><b>404 Not Found:</b> Occurs if the specified rental or user does not exist. The response
     *     includes an error message indicating which entity was not found.</li>
     *     <li><b>500 Internal Server Error:</b> A generic error message is returned if an unexpected condition
     *     was encountered and no more specific message is suitable.</li>
     * </ul>
     *
     * @param messageDTO The message data transfer object containing the rental ID, user ID, and message content.
     *                   Must be a valid object as defined by the MessageDTO class annotations.
     * @return A ResponseEntity containing either a success message or an error message, along with the appropriate
     * HTTP status code.
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
                    @ApiResponse(responseCode = "404", description = "Rental/User not found",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                                "error": "Rental/User not found"
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
    public ResponseEntity<Map<String, String>> createMessage(@RequestBody @Valid MessageDTO messageDTO) {
        try {
            messageService.createAndSaveMessage(messageDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Message sent successfully"));
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Could not send the message. Please try again later."));
        }
    }
}
