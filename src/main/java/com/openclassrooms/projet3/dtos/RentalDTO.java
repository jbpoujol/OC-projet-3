package com.openclassrooms.projet3.dtos;

import lombok.Data;

/**
 * Data Transfer Object representing rental property information.
 * <p>
 * Encapsulates details about a rental property that are transferred between the client and server.
 * This includes property identification, basic attributes, owner information, and timestamps for creation and updates.
 */
@Data
public class RentalDTO {

    /**
     * The unique identifier of the rental property.
     */
    private Long id;

    /**
     * The name of the rental property.
     */
    private String name;

    /**
     * The surface area of the rental property in square meters.
     */
    private int surface;

    /**
     * The rental price per month.
     */
    private double price;

    /**
     * The URL to a picture of the rental property.
     */
    private String picture;

    /**
     * A brief description of the rental property.
     */
    private String description;

    /**
     * The unique identifier of the owner of the rental property.
     */
    private Long owner_id;

    /**
     * The timestamp when the rental property was created.
     * <p>
     * Should be in ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ
     */
    private String created_at;

    /**
     * The timestamp when the rental property information was last updated.
     * <p>
     * Should be in ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ
     */
    private String updated_at;
}
