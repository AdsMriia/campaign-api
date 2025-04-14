package com.example.model.dto;

import com.example.model.CampaignStatus;
import com.example.model.CampaignType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO для передачи информации о кампании.
 */
@Data
public class CampaignDto {

    /**
     * Идентификатор кампании.
     */
    private UUID id;

    /**
     * Название кампании.
     */
    private String title;

    /**
     * Дата начала кампании.
     */
    private Long startDate;

    /**
     * Дата окончания кампании.
     */
    private Long endDate;

    /**
     * Идентификатор создателя кампании.
     */
    private UUID createdBy;

    /**
     * Тип кампании.
     */
    private CampaignType campaignType;

    /**
     * Статус кампании.
     */
    private CampaignStatus status;

    /**
     * Идентификатор рабочего пространства.
     */
    private UUID workspaceId;

    /**
     * Идентификатор канала.
     */
    private UUID channelId;

    /**
     * Флаг архивации кампании.
     */
    private Boolean isArchived;

    /**
     * Максимальное количество ретаргетируемых пользователей.
     */
    private Long maxRetargeted;

    /**
     * Процент аудитории для охвата.
     */
    private Integer audiencePercent;

    /**
     * Максимальная стоимость кампании.
     */
    private BigDecimal maxCost;

    /**
     * Сообщение об ошибке в случае неудачного запуска кампании.
     */
    private String errorMessage;

    /**
     * Дата создания кампании.
     */
    private Long createdAt;

    /**
     * Дата обновления кампании.
     */
    private Long updatedAt;

    /**
     * Список креативов кампании.
     */
    private List<CampaignCreativeDto> creatives;
}
