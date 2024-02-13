package com.openclassrooms.projet3.service;

import com.openclassrooms.projet3.model.Message;
import com.openclassrooms.projet3.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

}
