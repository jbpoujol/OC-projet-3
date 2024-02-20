package com.openclassrooms.projet3.excepton;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles constraint violation exceptions.
     * <p>
     * This method processes exceptions thrown due to constraint violations in request parameters or path variables,
     * constructing a detailed error response that includes the specific violations encountered.
     *
     * @param ex the caught ConstraintViolationException
     * @return a ResponseEntity containing the error details and a BAD_REQUEST status
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Validation error");
        List<String> errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());
        body.put("details", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles method argument type mismatches.
     * <p>
     * This method deals with exceptions that occur when a request parameter cannot be converted to the expected type,
     * returning an error response that highlights the invalid parameter.
     *
     * @param ex the caught MethodArgumentTypeMismatchException
     * @return a ResponseEntity with error details and a BAD_REQUEST status
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Invalid request parameter");
        body.put("details", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles custom "not found" exceptions.
     * <p>
     * This method responds to exceptions indicating that a requested resource was not found,
     * with an error response detailing the resource and reason it could not be found.
     *
     * @param ex the caught CustomNotFoundException
     * @return a ResponseEntity with error details and a NOT_FOUND status
     */
    @ExceptionHandler(CustomNotFoundException.class)
    public ResponseEntity<?> handleCustomNotFoundException(CustomNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Not Found");
        body.put("details", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles all other exceptions not specifically addressed by other @ExceptionHandler methods.
     * <p>
     * This generic method catches all other exceptions, providing a general error response
     * that includes a message about the unexpected error.
     *
     * @param ex      the caught Exception
     * @param request the WebRequest in which the exception occurred
     * @return a ResponseEntity with error details and an INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "An unexpected error occurred");
        body.put("details", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
