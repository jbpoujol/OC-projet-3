package com.openclassrooms.projet3.dtos;

/**
 * Represents a generic response structure for API calls.
 * This class is used to standardize the response format of the API, providing a consistent way to convey
 * success or failure, along with an optional message and data payload.
 */
public class ApiStandardResponse {

    /**
     * Indicates if the API call was successful.
     */
    private boolean success;

    /**
     * A message accompanying the response, typically used for providing details on errors or success notices.
     */
    private String message;

    /**
     * Optional data associated with the response. Can be any type of object including complex types or collections.
     */
    private Object data;

    /**
     * Constructs a new ApiResponse indicating success or failure, with a message.
     * This constructor is typically used when no additional data needs to be returned beyond a success indicator and message.
     *
     * @param success Indicates the success or failure of the API call.
     * @param message A message accompanying the response, providing additional details.
     */
    public ApiStandardResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null; // No data is associated by default.
    }

    /**
     * Constructs a new ApiResponse indicating success or failure, with a message and additional data.
     * This constructor is used to return additional data with the response.
     *
     * @param success Indicates the success or failure of the API call.
     * @param message A message accompanying the response, providing additional details.
     * @param data    Additional data to be returned with the response.
     */
    public ApiStandardResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * Constructs a new ApiResponse for a failure, with an error message.
     * This constructor sets {@code success} to false by default and is typically used to indicate an error condition.
     *
     * @param message An error message describing the failure.
     */
    public ApiStandardResponse(String message) {
        this.success = false;
        this.message = message;
        this.data = null; // No data is associated with failure by default.
    }

    // Getters and Setters

    /**
     * Returns {@code true} if the API call was successful, {@code false} otherwise.
     *
     * @return The success status of the API call.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success status of the API call.
     *
     * @param success The success status to set.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Returns the message associated with the API response.
     *
     * @return The message associated with the API response.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message associated with the API response.
     *
     * @param message The message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the data associated with the API response.
     *
     * @return The data associated with the API response. Could be {@code null} if no data is present.
     */
    public Object getData() {
        return data;
    }

    /**
     * Sets the data to be associated with the API response.
     *
     * @param data The data to set.
     */
    public void setData(Object data) {
        this.data = data;
    }
}
