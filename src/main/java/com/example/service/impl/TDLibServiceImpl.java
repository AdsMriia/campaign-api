package com.example.service.impl;

import com.example.client.TdLibClient;
import com.example.exception.FeignException;
import com.example.exception.ServiceUnavailableException;
import com.example.service.TDLibService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Реализация сервиса для взаимодействия с TdLib клиентом.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TDLibServiceImpl implements TDLibService {

    private final TdLibClient tdLibClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean submitCampaign(UUID campaignId) {
        log.info("Отправка кампании с ID: {}", campaignId);

        try {
            ResponseEntity<String> response = tdLibClient.startCampaign(campaignId);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Кампания {} успешно отправлена", campaignId);
                return true;
            } else {
                log.error("Ошибка при отправке кампании {}: {}", campaignId, response.getBody());
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка при взаимодействии с TdLib при отправке кампании {}: {}",
                    campaignId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean stopCampaign(UUID campaignId) {
        log.info("Остановка кампании с ID: {}", campaignId);

        try {
            ResponseEntity<String> response = tdLibClient.stopCampaign(campaignId);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Кампания {} успешно остановлена", campaignId);
                return true;
            } else {
                log.error("Ошибка при остановке кампании {}: {}", campaignId, response.getBody());
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка при взаимодействии с TdLib при остановке кампании {}: {}",
                    campaignId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean scheduleCampaign(UUID campaignId, Long timestamp, String timezone) {
        log.info("Планирование кампании с ID: {}, время: {}, часовой пояс: {}",
                campaignId, timestamp, timezone);

        try {
            ResponseEntity<String> response = tdLibClient.scheduleCampaign(campaignId, timestamp, timezone);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Кампания {} успешно запланирована на {}", campaignId, timestamp);
                return true;
            } else {
                log.error("Ошибка при планировании кампании {}: {}", campaignId, response.getBody());
                return false;
            }
        } catch (Exception e) {
            log.error("Ошибка при взаимодействии с TdLib при планировании кампании {}: {}",
                    campaignId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String checkCampaignStatus(UUID campaignId) {
        log.info("Проверка статуса кампании с ID: {}", campaignId);

        try {
            ResponseEntity<String> response = tdLibClient.checkStatus(campaignId);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Получен статус кампании {}: {}", campaignId, response.getBody());
                return response.getBody();
            } else {
                log.error("Ошибка при получении статуса кампании {}: {}", campaignId, response.getBody());
                return "ERROR: " + response.getStatusCode();
            }
        } catch (Exception e) {
            log.error("Ошибка при взаимодействии с TdLib при получении статуса кампании {}: {}",
                    campaignId, e.getMessage(), e);
            throw new ServiceUnavailableException("Сервис TdLib недоступен");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCampaignStats(UUID campaignId) {
        log.info("Получение статистики кампании с ID: {}", campaignId);

        try {
            ResponseEntity<String> response = tdLibClient.getStats(campaignId);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Получена статистика кампании {}", campaignId);
                return response.getBody();
            } else {
                log.error("Ошибка при получении статистики кампании {}: {}", campaignId, response.getBody());
                return "ERROR: " + response.getStatusCode();
            }
        } catch (Exception e) {
            log.error("Ошибка при взаимодействии с TdLib при получении статистики кампании {}: {}",
                    campaignId, e.getMessage(), e);
            throw new ServiceUnavailableException("Сервис TdLib недоступен");
        }
    }
}
