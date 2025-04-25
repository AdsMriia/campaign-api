package com.example.repository;

import com.example.entity.MediaToMessage;
import com.example.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MediaToMessageRepository extends JpaRepository<MediaToMessage, UUID> {
    void deleteAllByMessage(Message message);
}
