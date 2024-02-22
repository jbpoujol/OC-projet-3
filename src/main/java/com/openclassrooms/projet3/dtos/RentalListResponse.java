package com.openclassrooms.projet3.dtos;

import java.util.List;

/**
 * Represents a response structure for conveying a list of rental properties.
 * This class is designed to encapsulate a list of {@link RentalDTO} objects, facilitating structured data transfer,
 * particularly for API responses that return multiple rental property details.
 */
public class RentalListResponse {

    /**
     * A list of {@link RentalDTO} objects representing the rental properties.
     */
    private List<RentalDTO> rentals;

    /**
     * Constructs a new {@code RentalListResponse} with a specified list of rental properties.
     *
     * @param rentals A list of {@link RentalDTO} objects to be included in the response.
     */
    public RentalListResponse(List<RentalDTO> rentals) {
        this.rentals = rentals;
    }

    /**
     * Returns the list of rental properties encapsulated by this response object.
     *
     * @return A list of {@link RentalDTO} objects representing the rental properties.
     */
    public List<RentalDTO> getRentals() {
        return rentals;
    }

    /**
     * Sets or updates the list of rental properties encapsulated by this response object.
     *
     * @param rentals A list of {@link RentalDTO} objects representing the rental properties to be set.
     */
    public void setRentals(List<RentalDTO> rentals) {
        this.rentals = rentals;
    }
}
