package com.example.controller.impl;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.controller.PartnerLinkController;
import com.example.model.CampaignLinkStats;
import com.example.model.LinkStats;
import com.example.service.PartnerLinkService;
import com.example.util.UserAgentParser;
import com.example.util.UserAgentParser.UserAgentInfo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/partner-links")
@RequiredArgsConstructor
@Slf4j
public class PartnerLinkControllerImpl implements PartnerLinkController {

    private final PartnerLinkService partnerLinkService;

    @Override
    @GetMapping("/{id}/redirect")
    public ResponseEntity<Void> handleClick(
            @PathVariable UUID id,
            @RequestParam UUID userId,
            HttpServletRequest request) {
        // Получаем IP-адрес
        String ipAddress = getClientIpAddress(request);

        // Парсим User-Agent
        String userAgentString = request.getHeader("User-Agent");
        UserAgentInfo userAgentInfo = UserAgentParser.parseUserAgent(userAgentString);

        log.info("Клик по партнерской ссылке: IP: {}, Browser: {}, OS: {}, Device: {}",
                ipAddress,
                userAgentInfo.getBrowser(),
                userAgentInfo.getOperatingSystem(),
                userAgentInfo.getDeviceType());

        // Записываем информацию о клике с деталями
        partnerLinkService.recordClickWithDetails(id, userId, ipAddress, userAgentInfo);

        // Получаем оригинальный URL для редиректа
        String originalUrl = partnerLinkService.getPartnerLink(id).getOriginalUrl();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", originalUrl)
                .build();
    }

    @Override
    @GetMapping("/{id}/stats")
    public ResponseEntity<LinkStats> getStats(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID userId) {
        Long totalClicks = partnerLinkService.getClicksCount(id);
        Long userClicks = userId != null ? partnerLinkService.getUserClicksCount(id, userId) : 0L;

        return ResponseEntity.ok(new LinkStats(totalClicks, userClicks));
    }

    @Override
    @GetMapping("/campaign/{campaignId}/stats")
    public ResponseEntity<CampaignLinkStats> getCampaignStats(
            @PathVariable UUID campaignId) {
        Long totalClicks = partnerLinkService.getCampaignClicksCount(campaignId);
        return ResponseEntity.ok(new CampaignLinkStats(totalClicks));
    }

    /**
     * Получает IP-адрес клиента из запроса
     *
     * @param request HTTP запрос
     * @return IP-адрес клиента
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // Если в X-Forwarded-For содержится несколько IP-адресов, берем первый (клиентский)
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }
}
