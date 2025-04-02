package com.example.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.Campaign;
import com.example.entity.enums.CompanyStatus;
import com.example.model.CampaignStatus;
import com.example.model.CampaignType;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью Campaign.
 */
@Repository
public interface CampaignRepository extends JpaRepository<Campaign, UUID>, JpaSpecificationExecutor<Campaign> {

    /**
     * Находит все кампании, принадлежащие указанному рабочему пространству.
     *
     * @param workspaceId идентификатор рабочего пространства
     * @return список кампаний
     */
    List<Campaign> findAllByWorkspaceId(UUID workspaceId);

    /**
     * Находит все кампании, связанные с указанным каналом.
     *
     * @param channelId идентификатор канала
     * @return список кампаний
     */
    List<Campaign> findAllByChannelId(UUID channelId);

    /**
     * Находит все кампании с указанным статусом.
     *
     * @param status статус кампании
     * @return список кампаний
     */
    List<Campaign> findAllByStatus(CompanyStatus status);

    /**
     * Находит все не архивированные кампании.
     *
     * @return список кампаний
     */
    List<Campaign> findAllByIsArchivedFalse();

    /**
     * Находит все архивированные кампании.
     *
     * @return список кампаний
     */
    List<Campaign> findAllByIsArchivedTrue();

    /**
     * Проверяет, существует ли кампания с указанным заголовком.
     *
     * @param title заголовок кампании
     * @return true, если кампания существует, иначе false
     */
    boolean existsByTitle(String title);

    Optional<Campaign> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    @Query("SELECT c FROM Campaign c WHERE c.workspaceId = :workspaceId AND (:status IS NULL OR c.status = :status) "
            + "AND (:channelId IS NULL OR c.channelId = :channelId) "
            + "AND (:isArchived IS NULL OR c.isArchived = :isArchived)")
    Page<Campaign> findByWorkspaceIdWithFilters(
            @Param("workspaceId") UUID workspaceId,
            @Param("status") CampaignStatus status,
            @Param("channelId") UUID channelId,
            @Param("isArchived") Boolean isArchived,
            Pageable pageable);

    List<Campaign> findByCampaignTypeAndStatusAndStartDateBeforeAndEndDateAfter(
            CampaignType campaignType, CampaignStatus status, OffsetDateTime now, OffsetDateTime endDate);

    @Query("SELECT c FROM Campaign c WHERE c.status = :status AND c.startDate <= :now AND (c.endDate IS NULL OR c.endDate > :now)")
    List<Campaign> findActiveCampaigns(@Param("status") CampaignStatus status, @Param("now") OffsetDateTime now);

    @Query("SELECT DISTINCT EXTRACT(YEAR FROM c.startDate) as year, EXTRACT(MONTH FROM c.startDate) as month "
            + "FROM Campaign c WHERE c.workspaceId = :workspaceId ORDER BY year DESC, month DESC")
    List<Object[]> findDistinctCampaignIntervals(@Param("workspaceId") UUID workspaceId);

    boolean existsByTitleAndWorkspaceId(String title, UUID workspaceId);
}
