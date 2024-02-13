package com.openclassrooms.projet3.repository;

import com.openclassrooms.projet3.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
