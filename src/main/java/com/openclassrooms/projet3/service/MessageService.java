package com.openclassrooms.projet3.service;

import com.openclassrooms.projet3.dtos.MessageDTO;
import com.openclassrooms.projet3.excepton.CustomNotFoundException;
import com.openclassrooms.projet3.model.Message;

public interface MessageService {

    /**
     * Saves a message object to the database.
     * <p>
     * This method takes a Message entity as input and uses the messageRepository to persist
     * the entity to the database. It's a straightforward wrapper around the repository's save
     * method, providing an abstraction layer that could be extended with additional logic
     * (e.g., logging, events) in the future.
     *
     * @param message The Message entity to be saved. Assumes the entity is valid and ready to be persisted.
     */
    void saveMessage(Message message);

    /**
     * Creates and saves a new message based on the provided DTO.
     * <p>
     * This method orchestrates the process of creating a new message from a MessageDTO, which includes
     * looking up the associated Rental and DBUser entities based on IDs provided in the DTO. If either
     * the rental or the user is not found, a CustomNotFoundException is thrown. Once both entities are
     * verified to exist, a new Message entity is created, populated with the DTO and entity data, and
     * then saved to the database.
     * <p>
     * This method abstracts away the complexities of the entity creation and association, ensuring that
     * the service layer handles business logic and entity relationships.
     *
     * @param messageDTO The MessageDTO containing the data necessary to create a new message.
     *                   This includes the IDs for the associated rental and user, as well as the message content.
     * @throws CustomNotFoundException if either the rental or the user specified in the DTO does not exist.
     */
    void createAndSaveMessage(MessageDTO messageDTO) throws CustomNotFoundException;
}
