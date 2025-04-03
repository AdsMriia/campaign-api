package com.example.repository;

import com.example.entity.RetargetStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RetargetStatsRepository extends JpaRepository<RetargetStats, UUID> {

    List<RetargetStats> findByCampaignId(UUID campaignId);

    List<RetargetStats> findAllByCreatedAtBetween(OffsetDateTime startDate, OffsetDateTime endDate);

    @Query("SELECT SUM(r.sentMessageCount) FROM RetargetStats r WHERE r.campaignId = :campaignId")
    Long getTotalSentMessages(@Param("campaignId") UUID campaignId);

    @Query("SELECT SUM(r.retargetCount) FROM RetargetStats r WHERE r.campaignId = :campaignId")
    Long getTotalRetargetCount(@Param("campaignId") UUID campaignId);

    /**
     * Поиск статистики по идентификаторам каналов.
     *
     * @param channelIds список идентификаторов каналов
     * @param pageable параметры пагинации
     * @return страница со статистикой
     */
    @Query("SELECT rs FROM RetargetStats rs JOIN Campaign c ON rs.campaignId = c.id WHERE c.channelId IN :channelIds")
    Page<RetargetStats> findByChannelIds(@Param("channelIds") List<UUID> channelIds, Pageable pageable);

    /**
     * Поиск статистики по диапазону дат.
     *
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @param pageable параметры пагинации
     * @return страница со статистикой
     */
    Page<RetargetStats> findByCreatedAtBetween(OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable);

    /**
     * Алиас для метода findByCreatedAtBetween для соответствия названию,
     * используемому в сервисе.
     *
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @param pageable параметры пагинации
     * @return страница со статистикой
     */
    default Page<RetargetStats> findByDateRange(OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable) {
        return findByCreatedAtBetween(startDate, endDate, pageable);
    }

    /**
     * Поиск статистики по идентификаторам каналов и диапазону дат.
     *
     * @param channelIds список идентификаторов каналов
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @param pageable параметры пагинации
     * @return страница со статистикой
     */
    @Query("SELECT rs FROM RetargetStats rs JOIN Campaign c ON rs.campaignId = c.id "
            + "WHERE c.channelId IN :channelIds AND rs.createdAt BETWEEN :startDate AND :endDate")
    Page<RetargetStats> findByChannelIdsAndDateRange(
            @Param("channelIds") List<UUID> channelIds,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            Pageable pageable);
}
