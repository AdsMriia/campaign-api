package com.example.repository;

import com.example.entity.Message;
import com.example.model.MessageStatus;
import com.example.model.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID>, JpaSpecificationExecutor<Message> {

    Optional<Message> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    @Query("SELECT m FROM Message m WHERE m.workspaceId = :workspaceId "
            + "AND (:channelId IS NULL OR m.channelId = :channelId) "
            + "AND (:status IS NULL OR m.status = :status)")
    Page<Message> findByWorkspaceIdWithFilters(
            @Param("workspaceId") UUID workspaceId,
            @Param("channelId") UUID channelId,
            @Param("status") MessageStatus status,
            Pageable pageable);

    List<Message> findByWorkspaceIdAndChannelId(UUID workspaceId, UUID channelId);

    List<Message> findByWorkspaceIdIn(List<UUID> workspaceIds);

    Page<Message> findByWorkspaceIdAndType(UUID workspaceId, MessageType type, Pageable pageable);

    Page<Message> findByWorkspaceIdAndTypeAndStatus(UUID workspaceId, MessageType type, MessageStatus status, Pageable pageable);

    Page<Message> findByWorkspaceIdAndStatus(UUID workspaceId, MessageStatus status, Pageable pageable);

    Page<Message> findByWorkspaceId(UUID workspaceId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.workspaceId = :workspaceId")
    long countByWorkspaceId(UUID workspaceId);

    Message findByTelegramId(Integer telegramId);
}
