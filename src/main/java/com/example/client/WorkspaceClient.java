package com.example.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign клиент для взаимодействия с сервисом Workspace. Позволяет получать
 * информацию о рабочих пространствах и правах доступа.
 */
@FeignClient(name = "workspace", url = "${services.workspace}")
public interface WorkspaceClient {

    @GetMapping("/api/workspace/{workspaceId}/permissions")
    List<String> getPermissions(
            @PathVariable UUID workspaceId,
            @RequestHeader("Authorization") String token);

    /**
     * Получает информацию о рабочем пространстве по ID.
     *
     * @param workspaceId ID рабочего пространства
     * @param authorization JWT токен авторизации
     * @return информация о рабочем пространстве
     */
    @GetMapping("/api/workspaces/{workspaceId}")
    Object getWorkspaceById(
            @PathVariable("workspaceId") UUID workspaceId,
            @RequestParam("format") String format,
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
