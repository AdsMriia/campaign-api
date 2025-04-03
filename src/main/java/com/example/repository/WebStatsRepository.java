package com.example.repository;

import com.example.model.WebStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с веб-статистикой.
 */
@Repository
public interface WebStatsRepository extends JpaRepository<WebStats, UUID> {

    /**
     * Получить статистику по указанному типу, каналу и временному интервалу.
     *
     * @param channelId идентификатор канала
     * @param type тип статистики
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return список статистики
     */
    @Query("SELECT w FROM WebStats w WHERE w.channelId = :channelId AND w.type = :type "
            + "AND w.timestamp BETWEEN :startDate AND :endDate ORDER BY w.timestamp ASC")
    List<WebStats> findByChannelIdAndTypeAndTimestampBetween(
            @Param("channelId") UUID channelId,
            @Param("type") String type,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate);

    /**
     * Получить статистику сгруппированную по типу и каналу.
     *
     * @param channelIds список идентификаторов каналов
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return список агрегированной статистики
     */
    @Query("SELECT new com.example.model.dto.GroupedWebStats(w.type, w.channelTitle, COUNT(w)) "
            + "FROM WebStats w WHERE w.channelId IN :channelIds "
            + "AND w.timestamp BETWEEN :startDate AND :endDate "
            + "GROUP BY w.type, w.channelTitle ORDER BY COUNT(w) DESC")
    List<Object[]> findStatsGroupedByTypeAndChannel(
            @Param("channelIds") List<UUID> channelIds,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate);

    /**
     * Получить доступные временные метки для статистики.
     *
     * @param channelIds список идентификаторов каналов
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return список дат
     */
    @Query("SELECT DISTINCT FUNCTION('date_trunc', 'day', w.timestamp) "
            + "FROM WebStats w WHERE w.channelId IN :channelIds "
            + "AND w.timestamp BETWEEN :startDate AND :endDate "
            + "ORDER BY FUNCTION('date_trunc', 'day', w.timestamp) ASC")
    List<OffsetDateTime> findDistinctTimestamps(
            @Param("channelIds") List<UUID> channelIds,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate);
}
