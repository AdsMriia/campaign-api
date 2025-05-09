package com.example.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность для хранения информации о партнерских ссылках.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "partner_links")
public class PartnerLink {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Оригинальный URL партнерской ссылки.
     */
    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    /**
     * Идентификатор рабочего пространства.
     */
    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    /**
     * Идентификатор пользователя, создавшего ссылку.
     */
    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    /**
     * Идентификатор кампании, к которой относится ссылка.
     */
    @Column(name = "campaign_id", nullable = false)
    private Campaign campaignId;

    /**
     * Дата создания ссылки.
     */
    @CreationTimestamp
    private OffsetDateTime createdAt;

    /**
     * Количество кликов по ссылке.
     */
    @Column(name = "click_count")
    private Long clickCount = 0L;
}
