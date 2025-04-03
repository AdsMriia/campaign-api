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

    /**
     * Находит кампании с пагинацией по идентификатору рабочего пространства.
     *
     * @param workspaceId идентификатор рабочего пространства
     * @param pageable параметры пагинации
     * @return страница кампаний
     */
    Page<Campaign> findByWorkspaceId(UUID workspaceId, Pageable pageable);

    /**
     * Находит кампании с пагинацией по идентификаторам рабочих пространств.
     *
     * @param workspaceIds список идентификаторов рабочих пространств
     * @return список кампаний
     */
    List<Campaign> findByWorkspaceIdIn(List<UUID> workspaceIds);

    /**
     * Находит кампании с пагинацией по идентификатору рабочего пространства и
     * статусу.
     *
     * @param workspaceId идентификатор рабочего пространства
     * @param status статус кампании
     * @param pageable параметры пагинации
     * @return страница кампаний
     */
    Page<Campaign> findByWorkspaceIdAndStatus(UUID workspaceId, CampaignStatus status, Pageable pageable);

    /**
     * Находит кампании с пагинацией по идентификатору рабочего пространства и
     * флагу архивации.
     *
     * @param workspaceId идентификатор рабочего пространства
     * @param isArchived флаг архивации
     * @param pageable параметры пагинации
     * @return страница кампаний
     */
    Page<Campaign> findByWorkspaceIdAndIsArchived(UUID workspaceId, Boolean isArchived, Pageable pageable);

    /**
     * Находит кампании с пагинацией по идентификатору рабочего пространства и
     * идентификаторам каналов.
     *
     * @param workspaceId идентификатор рабочего пространства
     * @param channelIds список идентификаторов каналов
     * @param pageable параметры пагинации
     * @return страница кампаний
     */
    @Query("SELECT c FROM Campaign c WHERE c.workspaceId = :workspaceId AND c.channelId IN :channelIds")
    Page<Campaign> findByWorkspaceIdAndChannelIds(
            @Param("workspaceId") UUID workspaceId,
            @Param("channelIds") List<UUID> channelIds,
            Pageable pageable);

    /**
     * Находит кампании с пагинацией по идентификатору рабочего пространства,
     * статусу и флагу архивации.
     *
     * @param workspaceId идентификатор рабочего пространства
     * @param status статус кампании
     * @param isArchived флаг архивации
     * @param pageable параметры пагинации
     * @return страница кампаний
     */
    Page<Campaign> findByWorkspaceIdAndStatusAndIsArchived(
            UUID workspaceId,
            CampaignStatus status,
            Boolean isArchived,
            Pageable pageable);

    /**
     * Находит кампании с пагинацией по идентификатору рабочего пространства,
     * идентификаторам каналов и статусу.
     *
     * @param workspaceId идентификатор рабочего пространства
     * @param channelIds список идентификаторов каналов
     * @param status статус кампании
     * @param pageable параметры пагинации
     * @return страница кампаний
     */
    @Query("SELECT c FROM Campaign c WHERE c.workspaceId = :workspaceId AND c.channelId IN :channelIds AND c.status = :status")
    Page<Campaign> findByWorkspaceIdAndChannelIdsAndStatus(
            @Param("workspaceId") UUID workspaceId,
            @Param("channelIds") List<UUID> channelIds,
            @Param("status") CampaignStatus status,
            Pageable pageable);

    /**
     * Находит кампании с пагинацией по идентификатору рабочего пространства,
     * идентификаторам каналов и флагу архивации.
     *
     * @param workspaceId идентификатор рабочего пространства
     * @param channelIds список идентификаторов каналов
     * @param isArchived флаг архивации
     * @param pageable параметры пагинации
     * @return страница кампаний
     */
    @Query("SELECT c FROM Campaign c WHERE c.workspaceId = :workspaceId AND c.channelId IN :channelIds AND c.isArchived = :isArchived")
    Page<Campaign> findByWorkspaceIdAndChannelIdsAndIsArchived(
            @Param("workspaceId") UUID workspaceId,
            @Param("channelIds") List<UUID> channelIds,
            @Param("isArchived") Boolean isArchived,
            Pageable pageable);

    /**
     * Находит кампании с пагинацией по идентификатору рабочего пространства,
     * идентификаторам каналов, статусу и флагу архивации.
     *
     * @param workspaceId идентификатор рабочего пространства
     * @param channelIds список идентификаторов каналов
     * @param status статус кампании
     * @param isArchived флаг архивации
     * @param pageable параметры пагинации
     * @return страница кампаний
     */
    @Query("SELECT c FROM Campaign c WHERE c.workspaceId = :workspaceId AND c.channelId IN :channelIds "
            + "AND c.status = :status AND c.isArchived = :isArchived")
    Page<Campaign> findByWorkspaceIdAndChannelIdsAndStatusAndIsArchived(
            @Param("workspaceId") UUID workspaceId,
            @Param("channelIds") List<UUID> channelIds,
            @Param("status") CampaignStatus status,
            @Param("isArchived") Boolean isArchived,
            Pageable pageable);

    /**
     * Находит интервалы дат кампаний по идентификатору канала.
     *
     * @param channelId идентификатор канала
     * @return список объектов с годом и месяцем
     */
    @Query("SELECT DISTINCT EXTRACT(YEAR FROM c.startDate) as year, EXTRACT(MONTH FROM c.startDate) as month "
            + "FROM Campaign c WHERE c.channelId = :channelId ORDER BY year DESC, month DESC")
    List<Object[]> findCampaignDatesByChannelId(@Param("channelId") UUID channelId);

    List<Campaign> findByCampaignTypeAndStatusAndStartDateBeforeAndEndDateAfter(
            CampaignType campaignType, CampaignStatus status, OffsetDateTime now, OffsetDateTime endDate);

    @Query("SELECT c FROM Campaign c WHERE c.status = :status AND c.startDate <= :now AND (c.endDate IS NULL OR c.endDate > :now)")
    List<Campaign> findActiveCampaigns(@Param("status") CampaignStatus status, @Param("now") OffsetDateTime now);

    @Query("SELECT DISTINCT EXTRACT(YEAR FROM c.startDate) as year, EXTRACT(MONTH FROM c.startDate) as month "
            + "FROM Campaign c WHERE c.workspaceId = :workspaceId ORDER BY year DESC, month DESC")
    List<Object[]> findDistinctCampaignIntervals(@Param("workspaceId") UUID workspaceId);

    boolean existsByTitleAndWorkspaceId(String title, UUID workspaceId);
}
