package com.example.service;

import java.util.UUID;

import com.example.entity.PartnerLink;
import com.example.util.UserAgentParser.UserAgentInfo;

public interface PartnerLinkService {


    /**
     * Создает партнерскую ссылку
     * @param originalUrl исходный URL
     * @param workspaceId ID рабочей области
     * @param createdBy ID пользователя, создавшего ссылку
     * @param campaignId ID кампании
     * @return партнерская ссылка
     */
    PartnerLink createPartnerLink(String originalUrl, UUID workspaceId, UUID createdBy, UUID campaignId);

    /**
     * Генерирует шаблон URL для отслеживания кликов по партнерской ссылке
     *
     * @param partnerLinkId ID партнерской ссылки
     * @return шаблон URL для отслеживания кликов
     */
    String generateTrackingUrlTemplate(UUID partnerLinkId);

    /**
     * Записывает информацию о клике по партнерской ссылке
     *
     * @param partnerLinkId ID партнерской ссылки
     * @param userId ID пользователя
     */
    void recordClick(UUID partnerLinkId, UUID userId);

    /**
     * Записывает информацию о клике по партнерской ссылке с данными об IP и
     * User-Agent
     *
     * @param partnerLinkId ID партнерской ссылки
     * @param userId ID пользователя
     * @param ipAddress IP-адрес пользователя
     * @param userAgentInfo информация о браузере и устройстве пользователя
     */
    void recordClickWithDetails(UUID partnerLinkId, UUID userId, String ipAddress, UserAgentInfo userAgentInfo);

    /**
     * Получает партнерскую ссылку по ID
     *
     * @param id ID партнерской ссылки
     * @return партнерская ссылка
     */
    PartnerLink getPartnerLink(UUID id);

    /**
     * Получает количество кликов по партнерской ссылке
     *
     * @param partnerLinkId ID партнерской ссылки
     * @return количество кликов
     */
    Long getClicksCount(UUID partnerLinkId);

    /**
     * Получает количество кликов по партнерской ссылке для конкретного пользователя
     *
     * @param partnerLinkId ID партнерской ссылки
     * @param userId ID пользователя
     * @return количество кликов для конкретного пользователя
     */
    Long getUserClicksCount(UUID partnerLinkId, UUID userId);

    /**
     * Получает количество кликов по партнерской ссылке для конкретной кампании
     *
     * @param campaignId ID кампании
     * @return количество кликов для конкретной кампании
     */
    Long getCampaignClicksCount(UUID campaignId);

}
