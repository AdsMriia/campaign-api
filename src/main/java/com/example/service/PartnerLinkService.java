package com.example.service;

import java.util.UUID;

import com.example.entity.PartnerLink;
import com.example.model.dto.PartnerLinkJarvisDto;
import com.example.util.UserAgentParser.UserAgentInfo;

import jakarta.servlet.http.HttpServletRequest;

public interface PartnerLinkService {

    /**
     * Создает партнерскую ссылку
     *
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
     * @param botId ID бота
     */
    void recordClick(UUID partnerLinkId, Long userId, Long botId);

    /**
     * Получает язык устройства из заголовка Accept-Language
     *
     * @param request HTTP запрос
     * @return язык устройства
     */
    String getDeviceLanguage(HttpServletRequest request);

    /**
     * Записывает информацию о клике по партнерской ссылке с данными об IP и
     * User-Agent
     *
     * @param partnerLinkId ID партнерской ссылки
     * @param userId ID пользователя
     * @param ipAddress IP-адрес пользователя
     * @param userAgentInfo информация о браузере и устройстве пользователя
     */
    void recordClickWithDetails(UUID partnerLinkId, Long userId, Long botId, String ipAddress, UserAgentInfo userAgentInfo, HttpServletRequest request);

    /**
     * Получает партнерскую ссылку по ID
     *
     * @param id ID партнерской ссылки
     * @return партнерская ссылка
     */
    PartnerLink getPartnerLink(UUID id);

    /**
     * Создает партнерскую ссылку для Jarvis
     *
     * @param link оригинальная ссылка
     * @param userId ID пользователя
     * @return DTO с данными созданной партнерской ссылки
     */
    PartnerLinkJarvisDto createPartnerLinkJarvis(String link, Long telegramUserId);

    /**
     * Получает количество кликов по партнерской ссылке
     *
     * @param partnerLinkId ID партнерской ссылки
     * @return количество кликов
     */
    Long getClicksCount(UUID partnerLinkId);

    /**
     * Получает количество кликов по партнерской ссылке для конкретного
     * пользователя
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
