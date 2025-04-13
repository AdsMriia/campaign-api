package com.example.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "campaign_creatives")
public class CampaignCreative extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @Column
    private Integer percent;

    @Column(nullable = false)
    private Integer ordinal;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CampaignCreative campaignCreative = (CampaignCreative) o;
        return Objects.equals(getId(), campaignCreative.getId()) && Objects.equals(percent, campaignCreative.percent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), percent);
    }

    @Override
    public String toString() {
        return "{"
                + "\"id\": \"" + getId() + "\","
                + "\"percent\": " + getPercent() + ","
                + "\"ordinal\": " + getOrdinal()
                + "}";
    }
}
