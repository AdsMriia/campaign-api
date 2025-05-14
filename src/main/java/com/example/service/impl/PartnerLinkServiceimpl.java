package com.example.service.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.entity.Campaign;
import com.example.entity.ClickEvent;
import com.example.entity.PartnerLink;
import com.example.entity.PartnerLinkClick;
import com.example.entity.UserAgent;
import com.example.repository.CampaignRepository;
import com.example.repository.ClickEventRepository;
import com.example.repository.PartnerLinkClickRepository;
import com.example.repository.PartnerLinkRepository;
import com.example.repository.UserAgentRepository;
import com.example.service.PartnerLinkService;
import com.example.util.UserAgentParser.UserAgentInfo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartnerLinkServiceimpl implements PartnerLinkService {

    private final PartnerLinkRepository partnerLinkRepository;
    private final PartnerLinkClickRepository clickRepository;
    private final CampaignRepository campaignRepository;
    private final ClickEventRepository clickEventRepository;
    private final WebClient webClient;
    private final UserAgentRepository userAgentRepository;

    @Override
    @Transactional
    public PartnerLink createPartnerLink(String originalUrl, UUID workspaceId, UUID createdBy, UUID campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        PartnerLink partnerLink = new PartnerLink();
        partnerLink.setOriginalUrl(originalUrl);
        partnerLink.setWorkspaceId(workspaceId);
        partnerLink.setCreatedBy(createdBy);
        partnerLink.setCampaign(campaign);
        return partnerLinkRepository.save(partnerLink);
    }

    @Override
    @Transactional
    public String generateTrackingUrlTemplate(UUID partnerLinkId) {
        // {userId} - заменяется на id пользователя только это делать в ТГ
        return String.format("https://adsmriia.com/api/campaign/partner-links/%s/redirect/{userId}", partnerLinkId);
    }

    @Override
    @Transactional
    public void recordClick(UUID partnerLinkId, UUID userId) {
        PartnerLink partnerLink = getPartnerLink(partnerLinkId);
        PartnerLinkClick click = new PartnerLinkClick();
        click.setPartnerLink(partnerLink);
        click.setUserId(userId);
        clickRepository.save(click);
    }

    @Override
    @Transactional
    public String getDeviceLanguage(HttpServletRequest request) {
        String deviceLanguage = request.getHeader("Accept-Language");

        if (deviceLanguage == null || deviceLanguage.isEmpty()) {
            return "Unknown";
        }
        deviceLanguage = deviceLanguage.split(",")[0];

        return deviceLanguage;
    }

    @Override
    @Transactional
    public void recordClickWithDetails(UUID partnerLinkId, UUID userId, String ipAddress, UserAgentInfo userAgentInfo, HttpServletRequest request) {
        try {
            // Получаем партнерскую ссылку
            PartnerLink partnerLink = getPartnerLink(partnerLinkId);
            // Создаем объект UserAgent сразу, а не в асинхронном коде
            UserAgent userAgent = new UserAgent();
            userAgent.setLanguage(getDeviceLanguage(request));
            userAgent.setBrowser(userAgentInfo.getBrowser());
            userAgent.setBrowserVersion(userAgentInfo.getBrowserVersion());
            userAgent.setOperatingSystem(userAgentInfo.getOperatingSystem());
            userAgent.setDeviceType(userAgentInfo.getDeviceType());

            // Сохраняем UserAgent и получаем сохраненный объект с ID
            UserAgent savedUserAgent = userAgentRepository.save(userAgent);
            log.info("UserAgent сохранен: " + savedUserAgent);

            // Создаем объект для записи о клике
            ClickEvent clickEvent = ClickEvent.builder()
                    .partnerLink(partnerLink)
                    .userId(userId)
                    .ipAddress(ipAddress)
                    .build();

            // Сохраняем запись о клике
            clickEventRepository.save(clickEvent);
            log.info("ClickEvent сохранен: " + clickEvent);
            log.info("Запись о клике с расширенной информацией: partnerLinkId={}, userId={}, ipAddress={}, browser={}, os={}, device={}",
                    partnerLinkId, userId, ipAddress, userAgentInfo.getBrowser(),
                    userAgentInfo.getOperatingSystem(), userAgentInfo.getDeviceType());

            // Для обратной совместимости также сохраняем в старую таблицу
            recordClick(partnerLinkId, userId);

            // Асинхронно получаем информацию об IP-адресе
            if (ipAddress != null && !ipAddress.isEmpty()) {
                parseIpAddress(ipAddress)
                        .subscribe(
                                ipInfo -> {
                                    log.info("IP информация: {}", ipInfo);

                                    // Здесь можно обновить запись о клике с дополнительной информацией
                                    // Например, добавить страну, город, координаты и т.д.
                                    // Но для этого нужно добавить соответствующие поля в сущность ClickEvent
                                    String country = (String) ipInfo.get("country");
                                    String city = (String) ipInfo.get("city");
                                    String region = (String) ipInfo.get("region");
                                    String timezone = (String) ipInfo.get("timezone");

                                    // Обновляем существующий UserAgent с геоданными
                                    savedUserAgent.setCountry(country);
                                    savedUserAgent.setCity(city);
                                    savedUserAgent.setRegion(region);
                                    savedUserAgent.setTimezone(timezone);
                                    userAgentRepository.save(savedUserAgent);

                                    log.info("UserAgent обновлен с геоданными: " + savedUserAgent);

                                    log.info("IP геолокация: country={}, region={}, city={}, timezone={}",
                                            country, region, city, timezone);
                                },
                                error -> log.error("Ошибка при получении информации об IP: {}", error.getMessage())
                        );
            }

        } catch (Exception e) {
            log.error("Ошибка при записи о клике с расширенной информацией: {}", e.getMessage(), e);
            // Если произошла ошибка с новой таблицей, все равно пытаемся записать в старую
            recordClick(partnerLinkId, userId);
        }
    }

    /**
     * Получает информацию об IP-адресе через сервис ipinfo.io
     *
     * @param ipAddress IP-адрес
     * @return Mono с информацией об IP-адресе в виде Map
     */
    public Mono<Map<String, Object>> parseIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return Mono.empty();
        }

        // // Удаляем порт, если он есть в IP-адресе (например, 192.168.1.1:8080)
        // String cleanIp = ipAddress.contains(":") ? ipAddress.split(":")[0] : ipAddress;
        // // Проверяем, что IP-адрес не локальный
        // if (cleanIp.startsWith("127.") || cleanIp.startsWith("192.168.") || 
        //     cleanIp.startsWith("10.") || cleanIp.equals("localhost")) {
        //     log.debug("Локальный IP-адрес, пропускаем запрос к ipinfo.io: {}", cleanIp);
        //     return Mono.empty();
        // }
        String url = "https://ipinfo.io/" + ipAddress + "/json";

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .doOnSuccess(response -> log.debug("Получена информация об IP {}: {}", ipAddress, response))
                .doOnError(e -> log.error("Ошибка при запросе информации об IP {}: {}", ipAddress, e.getMessage()))
                .onErrorResume(WebClientResponseException.class, e -> {
                    log.error("HTTP ошибка при запросе к ipinfo.io: {} {}", e.getStatusCode(), e.getStatusText());
                    return Mono.empty();
                })
                .onErrorResume(e -> {
                    log.error("Непредвиденная ошибка при запросе к ipinfo.io: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerLink getPartnerLink(UUID id) {
        return partnerLinkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partner link not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Long getClicksCount(UUID partnerLinkId) {
        return partnerLinkRepository.getClicksCount(partnerLinkId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUserClicksCount(UUID partnerLinkId, UUID userId) {
        return partnerLinkRepository.getUserClicksCount(partnerLinkId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCampaignClicksCount(UUID campaignId) {
        return partnerLinkRepository.getCampaignClicksCount(campaignId);
    }
}
