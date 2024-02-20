package com.openclassrooms.projet3.service;

import com.openclassrooms.projet3.dtos.RentalDTO;
import com.openclassrooms.projet3.excepton.CustomNotFoundException;
import com.openclassrooms.projet3.model.Rental;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public interface RentalService {

    /**
     * Retrieves all rentals available in the repository.
     *
     * @return an iterable collection of all {@link Rental} entities.
     */
    Iterable<Rental> findAllRentals();

    /**
     * Fetches all rentals and converts them into a list of DTOs.
     * <p>
     * This method retrieves all rental entities, converts each to a {@link RentalDTO} to abstract
     * away entity details from clients, and returns a collection of these DTOs wrapped in a map
     * for response consistency.
     *
     * @return a map containing a list of {@link RentalDTO}s under the "rentals" key.
     */
    Map<String, Object> getRentalsWithDTOs();

    /**
     * Searches for a rental by its unique identifier.
     *
     * @param id the unique identifier of the rental to find.
     * @return an {@link Optional} of {@link Rental}, which will be empty if no rental is found with the provided ID.
     */
    Optional<Rental> findRentalById(Long id);

    /**
     * Finds a rental by its ID and converts it to a DTO.
     * <p>
     * This method attempts to find a rental by its unique ID. If found, the rental is converted to a {@link RentalDTO}
     * and returned. If not found, a {@link CustomNotFoundException} is thrown.
     *
     * @param id the unique identifier of the rental to find.
     * @return a {@link RentalDTO} representing the found rental.
     * @throws CustomNotFoundException if no rental is found with the given ID.
     */
    RentalDTO findRentalDTOById(Long id);

    /**
     * Creates a new rental with the provided details and saves it to the repository.
     * <p>
     * This method creates a new {@link Rental} entity, populating it with the provided details,
     * including uploading and storing the picture, if provided, and associating the rental with
     * an owner identified by their email. The new rental is then saved to the repository.
     *
     * @param name        the name of the rental.
     * @param surface     the surface area of the rental in square meters.
     * @param price       the price of the rental per month.
     * @param description a description of the rental.
     * @param picture     a picture of the rental, which is optional.
     * @param ownerEmail  the email of the rental's owner.
     * @return the newly created and saved {@link Rental} entity.
     * @throws Exception if the rental creation or saving process fails.
     */
    Rental createRental(String name, int surface, double price, String description, MultipartFile picture, String ownerEmail) throws Exception;

    /**
     * Updates the details of an existing rental.
     * <p>
     * This method updates an existing rental identified by its ID with the new details provided.
     * If a new picture is provided, it is stored and the rental's picture URL is updated. The method
     * checks if the authenticated user is the owner of the rental before applying updates.
     *
     * @param id          the ID of the rental to update.
     * @param name        the new name for the rental.
     * @param surface     the new surface area of the rental.
     * @param price       the new price for the rental.
     * @param description the new description of the rental.
     * @param picture     the new picture of the rental, which is optional.
     * @param ownerEmail  the email of the rental's owner, used to verify ownership.
     * @return the updated {@link Rental} entity.
     * @throws CustomNotFoundException if the rental or owner is not found, or if the user is not the owner.
     * @throws IOException             if an error occurs during picture upload.
     */
    Rental updateRental(Long id, String name, int surface, double price, String description, MultipartFile picture, String ownerEmail) throws CustomNotFoundException, IOException;

    /**
     * Deletes a rental by its ID.
     *
     * @param id the unique identifier of the rental to be deleted.
     */
    void deleteRental(Long id);

    /**
     * Checks if the authenticated user is the owner of the specified rental.
     *
     * @param rentalId the ID of the rental to check ownership against.
     * @return true if the authenticated user is the owner of the rental; false otherwise.
     * @throws RuntimeException if the rental is not found.
     */
    boolean isUserOwnerOfRental(Long rentalId);

}