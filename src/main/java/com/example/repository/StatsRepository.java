package com.example.repository;

import com.example.model.dto.GroupedWebStats;
import com.example.model.dto.HistoryDto;
import com.example.model.dto.PollStatsDto;
import com.example.model.dto.StatsDto;
import com.example.model.dto.WebStatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Репозиторий для работы со статистикой.
 */
@Repository
public interface StatsRepository extends JpaRepository<Object, UUID> {

    /**
     * Найти статистику по типу и ID канала.
     *
     * @param type тип статистики
     * @param channelId ID канала
     * @return список статистики
     */
    @Query(nativeQuery = true, value
            = "SELECT id, channel_id as channelId, channel_title as channelTitle, "
            + "type, value, created_at as timestamp "
            + "FROM stats WHERE type = :type AND channel_id = :channelId")
    List<WebStatsDto> findByTypeAndChannelId(@Param("type") String type, @Param("channelId") UUID channelId);

    /**
     * Найти сгруппированную статистику по типу и рабочему пространству.
     *
     * @param type тип статистики
     * @param workspaceId ID рабочего пространства
     * @return список сгруппированной статистики
     */
    @Query(nativeQuery = true, value
            = "SELECT type, channel_title as channelTitle, COUNT(*) as total "
            + "FROM stats WHERE type = :type AND workspace_id = :workspaceId "
            + "GROUP BY type, channel_title")
    List<GroupedWebStats> findGroupedStatsByTypeAndWorkspaceId(@Param("type") String type,
            @Param("workspaceId") UUID workspaceId);

    /**
     * Найти статистику по ID канала.
     *
     * @param channelId ID канала
     * @return список статистики
     */
    @Query(nativeQuery = true, value
            = "SELECT id, channel_id as channelId, channel_title as channelTitle, "
            + "type, value, created_at as timestamp "
            + "FROM stats WHERE channel_id = :channelId")
    List<WebStatsDto> findByChannelId(@Param("channelId") UUID channelId);

    /**
     * Найти административную статистику по ID канала и рабочему пространству.
     *
     * @param channelId ID канала
     * @param workspaceId ID рабочего пространства
     * @return список административной статистики
     */
    @Query(nativeQuery = true, value
            = "SELECT id, title, value, created_at as createdAt "
            + "FROM admin_stats WHERE channel_id = :channelId AND workspace_id = :workspaceId")
    List<StatsDto> findAdminStatsByChannelIdAndWorkspaceId(@Param("channelId") Long channelId,
            @Param("workspaceId") UUID workspaceId);

    /**
     * Получить данные для графика.
     *
     * @param type тип сообщения
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @param workspaceId ID рабочего пространства
     * @return список данных для графика
     */
    @Query(nativeQuery = true, value
            = "SELECT TO_CHAR(date_trunc('day', timestamp), 'YYYY-MM-DD') as date, "
            + "COUNT(*) as value FROM chart_data "
            + "WHERE message_type = :type AND timestamp BETWEEN :startDate AND :endDate "
            + "AND workspace_id = :workspaceId "
            + "GROUP BY date_trunc('day', timestamp) ORDER BY date_trunc('day', timestamp)")
    List<Map<String, Object>> getChartData(@Param("type") String type,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            @Param("workspaceId") UUID workspaceId);

    /**
     * Найти статистику опроса по ID.
     *
     * @param pollId ID опроса
     * @return статистика опроса
     */
    @Query(nativeQuery = true, value
            = "SELECT id, title, options, votes, created_at as createdAt "
            + "FROM poll_stats WHERE id = :pollId")
    PollStatsDto findPollStatsById(@Param("pollId") UUID pollId);

    /**
     * Найти все статистики опросов с фильтрацией.
     *
     * @param workspaceId ID рабочего пространства
     * @param channelIds список ID каналов
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @param pageable параметры пагинации
     * @return страница со статистикой опросов
     */
    @Query(nativeQuery = true, countQuery
            = "SELECT COUNT(*) FROM poll_stats WHERE workspace_id = :workspaceId "
            + "AND (:channelIds IS NULL OR channel_id IN :channelIds) "
            + "AND (:startDate IS NULL OR created_at >= :startDate) "
            + "AND (:endDate IS NULL OR created_at <= :endDate)",
            value
            = "SELECT id, title, options, votes, created_at as createdAt "
            + "FROM poll_stats WHERE workspace_id = :workspaceId "
            + "AND (:channelIds IS NULL OR channel_id IN :channelIds) "
            + "AND (:startDate IS NULL OR created_at >= :startDate) "
            + "AND (:endDate IS NULL OR created_at <= :endDate)")
    Page<PollStatsDto> findAllPollStats(@Param("workspaceId") UUID workspaceId,
            @Param("channelIds") List<UUID> channelIds,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            Pageable pageable);
}
