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
@Table(name = "retarget_stats")
public class RetargetStats extends BaseEntity {

    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", insertable = false, updatable = false)
    private Campaign campaign;

    @Column(name = "sent_message_count")
    private Integer sentMessageCount = 0;

    @Column(name = "retarget_count")
    private Integer retargetCount = 0;

    @Column(name = "target_count")
    private Integer targetCount = 0;

    @Column(name = "delivered_count")
    private Integer deliveredCount = 0;

    @Column(name = "click_count")
    private Integer clickCount = 0;
}
