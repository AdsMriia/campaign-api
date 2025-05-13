package com.example.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.config.FeignConfiguration;

/**
 * Feign клиент для взаимодействия с сервисом каналов.
 */
@FeignClient(name = "channel", url = "${services.channel}", configuration = FeignConfiguration.class)
public interface ChannelClient {

    /**
     * Получает информацию о канале по его ID.
     *
     * @param id идентификатор канала
     * @return информация о канале
     */
    @GetMapping("/channels/{id}")
    ResponseEntity<Object> getById(@RequestHeader("Authorization") String authorization, @PathVariable("id") UUID id);

    /**
     * Проверяет существование канала по его ID и ID рабочего пространства.
     *
     * @param id идентификатор канала
     * @param workspaceId идентификатор рабочего пространства
     * @return true, если канал существует, иначе false
     */
    @GetMapping("/channels/exists")
    boolean existsByIdAndWorkspaceId(@RequestParam("id") UUID id, @RequestParam("workspaceId") UUID workspaceId);

    /**
     * Подсчитывает количество подписчиков для канала.
     *
     * @param channelId идентификатор канала
     * @return количество подписчиков
     */
    @GetMapping("/channels/{channelId}/members/count/retarget")
    Long countSubscribersById(@PathVariable("channelId") UUID channelId, @RequestHeader("Authorization") String authorization);
}
