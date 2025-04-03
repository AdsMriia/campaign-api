package com.example.repository;

import com.example.entity.CampaignCreative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с креативами кампаний.
 */
@Repository
public interface CampaignCreativeRepository extends JpaRepository<CampaignCreative, UUID> {

    /**
     * Находит все креативы, связанные с указанной кампанией.
     *
     * @param campaignId идентификатор кампании
     * @return список креативов
     */
    List<CampaignCreative> findByCampaignId(UUID campaignId);

    /**
     * Удаляет все креативы, связанные с указанной кампанией.
     *
     * @param campaignId идентификатор кампании
     */
    void deleteByCampaignId(UUID campaignId);
}
