package com.example.repository;

import com.example.model.dto.HistoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с историей статистики.
 */
@Repository
public interface StatsHistoryRepository extends JpaRepository<Object, UUID> {

    /**
     * Найти историю по ID статистики.
     *
     * @param statsId ID статистики
     * @return список записей истории
     */
    @Query(nativeQuery = true, value
            = "SELECT id, stats_id as statsId, value, timestamp "
            + "FROM stats_history WHERE stats_id = :statsId "
            + "ORDER BY timestamp DESC")
    List<HistoryDto> findByStatsId(@Param("statsId") UUID statsId);
}
