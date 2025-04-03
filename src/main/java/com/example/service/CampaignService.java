package com.example.service;

import com.example.model.CampaignStatus;
import com.example.model.dto.CampaignDto;
import com.example.model.dto.ChannelCampaignDatesDto;
import com.example.model.dto.ExpectedRetargetDto;
import com.example.model.dto.RetargetStatsDto;
import com.example.model.dto.SubmitABDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с кампаниями.
 */
public interface CampaignService {

    /**
     * Создает и отправляет немедленную кампанию.
     *
     * @param submitABDto данные для создания кампании
     * @return список созданных кампаний
     */
    List<CampaignDto> immediateSubmit(SubmitABDto submitABDto);

    /**
     * Создает и отправляет обычную кампанию.
     *
     * @param submitABDto данные для создания кампании
     * @param timezone часовой пояс
     * @return список созданных кампаний
     */
    List<CampaignDto> campaignBasicSubmit(SubmitABDto submitABDto, String timezone);

    /**
     * Запускает ретаргетинг для кампании.
     *
     * @param channelId идентификатор канала
     * @param timestamp метка времени
     * @param campaignId идентификатор кампании
     * @param timezone часовой пояс
     * @return опциональная строка с ошибкой или пустой, если операция успешна
     */
    Optional<String> retarget(UUID channelId, Long timestamp, UUID campaignId, String timezone);

    /**
     * Получает статистику кампании.
     *
     * @param campaignId идентификатор кампании
     * @return статистика кампании
     */
    RetargetStatsDto getStats(UUID campaignId);

    /**
     * Получает всю статистику кампаний с возможностью фильтрации.
     *
     * @param page номер страницы
     * @param size размер страницы
     * @param asc порядок сортировки
     * @param sort поле сортировки
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @param channelId список идентификаторов каналов
     * @return страница со статистикой кампаний
     */
    Page<RetargetStatsDto> getAllStats(Integer page, Integer size, Boolean asc, String sort, Long startDate, Long endDate, List<UUID> channelId);

    /**
     * Останавливает ретаргетинг для кампании.
     *
     * @param campaignId идентификатор кампании
     * @return true, если ретаргетинг успешно остановлен
     */
    boolean stopRetarget(UUID campaignId);

    /**
     * Останавливает активную кампанию.
     *
     * @param campaignId идентификатор кампании
     * @return true, если кампания успешно остановлена
     */
    boolean stopCampaign(UUID campaignId);

    /**
     * Получает список кампаний с возможностью фильтрации.
     *
     * @param channelIds список идентификаторов каналов
     * @param page номер страницы
     * @param status статус кампании
     * @param size размер страницы
     * @param asc порядок сортировки
     * @param sort поле сортировки
     * @param isArchived флаг архивации
     * @return страница с кампаниями
     */
    Page<CampaignDto> getAll(List<UUID> channelIds, Integer page, CampaignStatus status, Integer size, Boolean asc, String sort, Boolean isArchived);

    /**
     * Получает кампанию по идентификатору.
     *
     * @param campaignId идентификатор кампании
     * @return информация о кампании
     */
    CampaignDto getByCampaignId(UUID campaignId);

    /**
     * Архивирует кампанию.
     *
     * @param campaignId идентификатор кампании
     * @return обновленная информация о кампании
     */
    CampaignDto archiveCampaign(UUID campaignId);

    /**
     * Получает интервалы дат кампаний.
     *
     * @param channelIds список идентификаторов каналов
     * @return список интервалов дат
     */
    List<ChannelCampaignDatesDto> getCampaignIntervalDate(List<UUID> channelIds);

    /**
     * Рассчитывает максимальное количество подписчиков для каналов.
     *
     * @param channelIds список идентификаторов каналов
     * @return список с информацией о потенциальном ретаргетинге
     */
    List<ExpectedRetargetDto> maxSubCount(List<UUID> channelIds);

    /**
     * Получает все кампании для указанных рабочих пространств.
     *
     * @param workspaceIds список идентификаторов рабочих пространств
     * @return список кампаний
     */
    List<CampaignDto> getAllCampaign(List<UUID> workspaceIds);
}
