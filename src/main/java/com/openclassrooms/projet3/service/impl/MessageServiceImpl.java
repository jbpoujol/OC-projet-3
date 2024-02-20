package com.openclassrooms.projet3.service.impl;

import com.openclassrooms.projet3.dtos.MessageDTO;
import com.openclassrooms.projet3.excepton.CustomNotFoundException;
import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.model.Message;
import com.openclassrooms.projet3.model.Rental;
import com.openclassrooms.projet3.repository.MessageRepository;
import com.openclassrooms.projet3.service.DBUserService;
import com.openclassrooms.projet3.service.MessageService;
import com.openclassrooms.projet3.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RentalService rentalService;

    @Autowired
    private DBUserService dbUserService;

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    public void createAndSaveMessage(MessageDTO messageDTO) throws CustomNotFoundException {

        Rental rental = rentalService.findRentalById(messageDTO.getRental_id())
                .orElseThrow(() -> new CustomNotFoundException("Rental not found"));

        DBUser user = dbUserService.findUserById(messageDTO.getUser_id())
                .orElseThrow(() -> new CustomNotFoundException("User not found"));

        Message message = new Message();
        message.setRental(rental);
        message.setUser(user);
        message.setMessage(messageDTO.getMessage());

        saveMessage(message);
    }


}
