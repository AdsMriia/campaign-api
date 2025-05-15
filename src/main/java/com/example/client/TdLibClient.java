package com.example.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.config.FeignConfiguration;
import com.example.model.dto.CampaignDto;
import com.example.model.dto.WebUserDtoShort;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * Feign клиент для взаимодействия с TdLib сервисом. Предоставляет методы для
 * запуска, планирования и остановки рекламных кампаний.
 */
@FeignClient(name = "telegram", url = "${services.telegram}", configuration = FeignConfiguration.class)
public interface TdLibClient {

    /**
     * Запускает немедленную кампанию.
     *
     * @param authorization JWT токен авторизации
     * @param campaignId ID кампании
     * @return ResponseEntity с результатом операции
     */
    @PostMapping("/campaign/start")
    ResponseEntity<String> startCampaign(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("campaignId") UUID campaignId);

    /**
     * Планирует запуск кампании на определенное время.
     *
     * @param authorization JWT токен авторизации
     * @return ResponseEntity с результатом операции
     */
    @PostMapping("/campaign/init")
    ResponseEntity<String> scheduleCampaign(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CampaignDto campaignDto
    );


   


    /**
     * Останавливает активную кампанию.
     *
     * @param authorization JWT токен авторизации
     * @param campaignId ID кампании
     * @return ResponseEntity с результатом операции
     */
    @PostMapping("/campaign/stop")
    ResponseEntity<String> stopCampaign(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("campaignId") UUID campaignId);

    /**
     * Инициализирует ретаргетинг для кампании.
     *
     * @param authorization JWT токен авторизации
     * @param channelId ID канала
     * @param timestamp timestamp начала ретаргетинга
     * @param campaignId ID кампании
     * @param timezone часовой пояс
     * @return ResponseEntity с результатом операции
     */
    @PostMapping("/initialize/cycle")
    ResponseEntity<String> initializeRetarget(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("channelId") UUID channelId,
            @RequestParam("timestamp") Long timestamp,
            @RequestParam("campaignId") UUID campaignId,
            @RequestParam("timezone") String timezone);

    /**
     * Останавливает ретаргетинг для кампании.
     *
     * @param authorization JWT токен авторизации
     * @param campaignId ID кампании
     * @return ResponseEntity с результатом операции
     */
    @PostMapping("/retarget/stop")
    ResponseEntity<String> stopRetarget(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("campaignId") UUID campaignId);

    /**
     * Получение статуса кампании
     *
     * @param authorization JWT токен авторизации
     * @param campaignId идентификатор кампании
     * @return ответ с информацией о статусе
     */
    @PostMapping("/campaign/checkStatus")
    ResponseEntity<String> checkStatus(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("campaignId") UUID campaignId);

    /**
     * Получение статистики кампании
     *
     * @param authorization JWT токен авторизации
     * @param campaignId идентификатор кампании
     * @return ответ со статистикой
     */
    @PostMapping("/campaign/getStats")
    ResponseEntity<String> getStats(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("campaignId") UUID campaignId);

    // @PostMapping("/split")
    // ResponseEntity<Map<UUID, List<ChannelMember>>> splitAudienceByPercentages(
    //         @RequestHeader("Authorization") String authorization,
    //         @RequestBody CampaignDto campaignDto);
}
