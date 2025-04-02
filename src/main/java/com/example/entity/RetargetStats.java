package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(name = "sent_message_count")
    private Integer sentMessageCount = 0;

    @Column(name = "retarget_count")
    private Integer retargetCount = 0;
}
