package com.example.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;
import java.util.UUID;

/**
 * Feign клиент для взаимодействия с сервисом Workspace. Позволяет получать
 * информацию о рабочих пространствах и правах доступа.
 */
@FeignClient(name = "workspace", url = "${workspace.url:http://workspace:8080}")
public interface WorkspaceClient {

    /**
     * Получает информацию о рабочем пространстве по ID.
     *
     * @param workspaceId ID рабочего пространства
     * @param authorization JWT токен авторизации
     * @return информация о рабочем пространстве
     */
    @GetMapping("/api/workspaces/{workspaceId}")
    Map<String, Object> getWorkspaceById(
            @PathVariable("workspaceId") UUID workspaceId,
            @RequestHeader("Authorization") String authorization);

    /**
     * Проверяет доступность канала для пользователя в рамках рабочего
     * пространства.
     *
     * @param workspaceId ID рабочего пространства
     * @param channelId ID канала
     * @param authorization JWT токен авторизации
     * @return результат проверки доступа
     */
    @GetMapping("/api/workspaces/{workspaceId}/channels/{channelId}/access")
    Map<String, Object> checkChannelAccess(
            @PathVariable("workspaceId") UUID workspaceId,
            @PathVariable("channelId") UUID channelId,
            @RequestHeader("Authorization") String authorization);
}
