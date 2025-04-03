package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "campaign_creatives")
public class CampaignCreative extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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
        return Objects.equals(id, campaignCreative.id) && Objects.equals(percent, campaignCreative.percent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, percent);
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
