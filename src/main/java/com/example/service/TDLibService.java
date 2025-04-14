package com.example.service;

import java.util.UUID;

import com.example.model.dto.CampaignDto;

/**
 * Сервис для взаимодействия с TdLib API.
 */
public interface TDLibService {

    /**
     * Отправить кампанию на выполнение.
     *
     * @param campaignId идентификатор кампании
     * @return true если запрос выполнен успешно, иначе false
     */
    boolean submitCampaign(UUID campaignId);

    /**
     * Остановить выполнение кампании.
     *
     * @param campaignId идентификатор кампании
     * @return true если запрос выполнен успешно, иначе false
     */
    boolean stopCampaign(UUID campaignId);

    /**
     * Запланировать выполнение кампании на указанное время.
     *
     * @param campaignDto объект, содержащий информацию о кампании
     * @return true если запрос выполнен успешно, иначе false
     */
    boolean scheduleCampaign(CampaignDto campaignDto);

    /**
     * Проверить статус выполнения кампании.
     *
     * @param campaignId идентификатор кампании
     * @return строка, содержащая статус кампании
     */
    String checkCampaignStatus(UUID campaignId);

    /**
     * Получить статистику по кампании.
     *
     * @param campaignId идентификатор кампании
     * @return строка, содержащая статистику в формате JSON
     */
    String getCampaignStats(UUID campaignId);
}
