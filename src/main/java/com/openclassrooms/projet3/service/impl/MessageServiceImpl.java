package com.openclassrooms.projet3.service.impl;

import com.openclassrooms.projet3.model.Message;
import com.openclassrooms.projet3.repository.MessageRepository;
import com.openclassrooms.projet3.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

}
