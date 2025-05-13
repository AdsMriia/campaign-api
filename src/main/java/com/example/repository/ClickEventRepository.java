package com.example.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.ClickEvent;

@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent, UUID> {

    /**
     * Подсчитывает количество кликов по определенной партнерской ссылке
     *
     * @param partnerLinkId ID партнерской ссылки
     * @return количество кликов
     */
    @Query("SELECT COUNT(c) FROM ClickEvent c WHERE c.partnerLink.id = :partnerLinkId")
    Long countByPartnerLinkId(@Param("partnerLinkId") UUID partnerLinkId);

    /**
     * Подсчитывает количество кликов по определенной партнерской ссылке для
     * конкретного пользователя
     *
     * @param partnerLinkId ID партнерской ссылки
     * @param userId ID пользователя
     * @return количество кликов
     */
    @Query("SELECT COUNT(c) FROM ClickEvent c WHERE c.partnerLink.id = :partnerLinkId AND c.userId = :userId")
    Long countByPartnerLinkIdAndUserId(@Param("partnerLinkId") UUID partnerLinkId, @Param("userId") UUID userId);

    /**
     * Подсчитывает количество кликов для определенной кампании
     *
     * @param campaignId ID кампании
     * @return количество кликов
     */
    @Query("SELECT COUNT(c) FROM ClickEvent c WHERE c.partnerLink.campaignId = :campaignId")
    Long countByCampaignId(@Param("campaignId") UUID campaignId);
}
