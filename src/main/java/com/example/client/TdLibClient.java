package com.example.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

/**
 * Feign клиент для взаимодействия с TdLib сервисом. Предоставляет методы для
 * запуска, планирования и остановки рекламных кампаний.
 */
@FeignClient(name = "tdlib", url = "${tdlib.url:http://tdlib:8090}")
public interface TdLibClient {

    /**
     * Запускает немедленную кампанию.
     *
     * @param campaignId ID кампании
     * @return ResponseEntity с результатом операции
     */
    @PostMapping("/api/campaign/start")
    ResponseEntity<String> startCampaign(@RequestParam("campaignId") UUID campaignId);

    /**
     * Планирует запуск кампании на определенное время.
     *
     * @param campaignId ID кампании
     * @param startTimestamp timestamp начала кампании
     * @param timezone часовой пояс
     * @return ResponseEntity с результатом операции
     */
    @PostMapping("/api/campaign/schedule")
    ResponseEntity<String> scheduleCampaign(
            @RequestParam("campaignId") UUID campaignId,
            @RequestParam("startTimestamp") Long startTimestamp,
            @RequestParam("timezone") String timezone);

    /**
     * Останавливает активную кампанию.
     *
     * @param campaignId ID кампании
     * @return ResponseEntity с результатом операции
     */
    @PostMapping("/api/campaign/stop")
    ResponseEntity<String> stopCampaign(@RequestParam("campaignId") UUID campaignId);

    /**
     * Инициализирует ретаргетинг для кампании.
     *
     * @param channelId ID канала
     * @param timestamp timestamp начала ретаргетинга
     * @param campaignId ID кампании
     * @param timezone часовой пояс
     * @return ResponseEntity с результатом операции
     */
    @PostMapping("/api/initialize/cycle")
    ResponseEntity<String> initializeRetarget(
            @RequestParam("channelId") UUID channelId,
            @RequestParam("timestamp") Long timestamp,
            @RequestParam("campaignId") UUID campaignId,
            @RequestParam("timezone") String timezone);

    /**
     * Останавливает ретаргетинг для кампании.
     *
     * @param campaignId ID кампании
     * @return ResponseEntity с результатом операции
     */
    @PostMapping("/api/retarget/stop")
    ResponseEntity<String> stopRetarget(@RequestParam("campaignId") UUID campaignId);
}
