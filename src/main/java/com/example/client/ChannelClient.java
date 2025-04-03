package com.example.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

/**
 * Feign клиент для взаимодействия с сервисом каналов.
 */
@FeignClient(name = "channel-service", url = "${channel.service.url:http://channel-service:8080}")
public interface ChannelClient {

    /**
     * Проверяет существование канала по его ID и ID рабочего пространства.
     *
     * @param id идентификатор канала
     * @param workspaceId идентификатор рабочего пространства
     * @return true, если канал существует, иначе false
     */
    @GetMapping("/api/channels/exists")
    boolean existsByIdAndWorkspaceId(@RequestParam("id") UUID id, @RequestParam("workspaceId") UUID workspaceId);

    /**
     * Подсчитывает количество подписчиков для канала.
     *
     * @param channelId идентификатор канала
     * @return количество подписчиков
     */
    @GetMapping("/api/channels/{channelId}/subscribers/count")
    Long countSubscribersById(@PathVariable("channelId") UUID channelId);
}
