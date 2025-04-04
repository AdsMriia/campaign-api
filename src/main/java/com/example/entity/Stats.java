package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Сущность статистики.
 */
@Entity
@Table(name = "stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stats {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "channel_id")
    private UUID channelId;

    @Column(name = "channel_title")
    private String channelTitle;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "type")
    private String type;

    @Column(name = "value")
    private String value;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
