package com.example.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.CampaignToSubscribers;

/**
 * Репозиторий для работы с сущностью CampaignToSubscribers.
 */
@Repository
public interface CampaignToSubscribersRepository extends JpaRepository<CampaignToSubscribers, UUID> {

    /**
     * Находит все связи для указанной кампании.
     *
     * @param campaignId идентификатор кампании
     * @return список связей
     */
    @Query("SELECT cts FROM CampaignToSubscribers cts WHERE cts.campaign.id = :campaignId")
    List<CampaignToSubscribers> findAllByCampaign(@Param("campaignId") UUID campaignId);

    /**
     * Находит все связи для указанного подписчика.
     *
     * @param subscriberId идентификатор подписчика
     * @return список связей
     */
    List<CampaignToSubscribers> findAllBySubscriberId(UUID subscriberId);

    /**
     * Находит все связи для указанной кампании и креатива.
     *
     * @param campaignId идентификатор кампании
     * @param creativeId идентификатор креатива
     * @return список связей
     */
    @Query("SELECT cts FROM CampaignToSubscribers cts WHERE cts.campaign.id = :campaignId AND cts.campaignCreative.id = :creativeId")
    List<CampaignToSubscribers> findAllByCampaignAndCreative(@Param("campaignId") UUID campaignId, @Param("creativeId") UUID creativeId);

    /**
     * Находит все связи с успешным ретаргетингом для указанной кампании.
     *
     * @param campaignId идентификатор кампании
     * @return список связей
     */
    @Query("SELECT cts FROM CampaignToSubscribers cts WHERE cts.campaign.id = :campaignId AND cts.retargeted = true")
    List<CampaignToSubscribers> findAllRetargetedByCampaign(@Param("campaignId") UUID campaignId);

    /**
     * Подсчитывает количество успешных ретаргетингов для указанной кампании.
     *
     * @param campaignId идентификатор кампании
     * @return количество успешных ретаргетингов
     */
    @Query("SELECT COUNT(cts) FROM CampaignToSubscribers cts WHERE cts.campaign.id = :campaignId AND cts.retargeted = true")
    Long countRetargetedByCampaign(@Param("campaignId") UUID campaignId);

    /**
     * Подсчитывает количество успешных ретаргетингов для указанного креатива.
     *
     * @param creativeId идентификатор креатива
     * @return количество успешных ретаргетингов
     */
    @Query("SELECT COUNT(cts) FROM CampaignToSubscribers cts WHERE cts.campaignCreative.id = :creativeId AND cts.retargeted = true")
    Long countRetargetedByCreative(@Param("creativeId") UUID creativeId);
}
