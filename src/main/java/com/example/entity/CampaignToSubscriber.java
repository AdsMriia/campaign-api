package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "campaign_to_subscribers")
public class CampaignToSubscriber extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @Column(name = "subscriber_id", nullable = false)
    private UUID subscriberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creative_id")
    private Message creative;

    @Column(name = "retargeted", nullable = false)
    private Boolean retargeted = false;
}
