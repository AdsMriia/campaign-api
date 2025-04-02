package com.example.entity;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность для хранения связей между кампаниями и подписчиками.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "campaign_to_subscribers")
public class CampaignToSubscribers {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @Column(name = "subscriber_id")
    private UUID subscriberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creative_id")
    private CampaignCreative campaignCreative;

    @Column(name = "retargeted")
    private Boolean retargeted = false;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CampaignToSubscribers that = (CampaignToSubscribers) o;
        return Objects.equals(id, that.id) && Objects.equals(campaign, that.campaign) && Objects.equals(subscriberId, that.subscriberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, campaign, subscriberId);
    }

    @Override
    public String toString() {
        return "{"
                + "\"id\": \"" + getId() + "\","
                + "\"campaign\": \"" + getCampaign().getId() + "\","
                + "\"subscriberId\": \"" + getSubscriberId() + "\","
                + "\"campaignCreative\": \"" + (getCampaignCreative() != null ? getCampaignCreative().getId() : null) + "\","
                + "\"retargeted\": " + getRetargeted() + ","
                + "\"createdAt\": \"" + getCreatedAt() + "\""
                + "}";
    }
}
