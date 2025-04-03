package com.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Сущность для хранения веб-статистики.
 */
@Entity
@Table(name = "web_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebStats {

    /**
     * Уникальный идентификатор статистики.
     */
    @Id
    @Column(name = "id")
    private UUID id;

    /**
     * Тип статистики.
     */
    @Column(name = "type", nullable = false)
    private String type;

    /**
     * Значение статистики.
     */
    @Column(name = "value", nullable = false)
    private Long value;

    /**
     * Временная метка события.
     */
    @Column(name = "timestamp", nullable = false)
    private OffsetDateTime timestamp;

    /**
     * Идентификатор канала.
     */
    @Column(name = "channel_id", nullable = false)
    private UUID channelId;

    /**
     * Название канала.
     */
    @Column(name = "channel_title")
    private String channelTitle;
}
