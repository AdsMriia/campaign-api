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
 * Сущность истории статистики.
 */
@Entity
@Table(name = "stats_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsHistory {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "stats_id")
    private UUID statsId;

    @Column(name = "value")
    private String value;

    @Column(name = "timestamp")
    private OffsetDateTime timestamp;
}
