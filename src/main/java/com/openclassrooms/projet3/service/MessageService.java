package com.openclassrooms.projet3.service;

import com.openclassrooms.projet3.dtos.MessageDTO;
import com.openclassrooms.projet3.excepton.CustomNotFoundException;
import com.openclassrooms.projet3.model.Message;

public interface MessageService {

    /**
     * Saves a message entity to the repository.
     *
     * @param message The message entity to be saved.
     */
    void saveMessage(Message message);

    /**
     * Creates a new message from a DTO and saves it.
     * <p>
     * This method fetches the associated Rental and DBUser based on IDs provided in the
     * MessageDTO. It then creates a new Message entity and saves it to the repository.
     *
     * @param messageDTO The DTO containing the message details.
     * @throws CustomNotFoundException if the rental or user specified in the DTO does not exist.
     */
    void createAndSaveMessage(MessageDTO messageDTO) throws CustomNotFoundException;
}
