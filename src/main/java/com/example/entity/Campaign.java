package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.enums.CampaignType;
import org.example.entity.enums.CompanyStatus;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ab_tables")
public class Campaign {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "table_name")
    private String title;

    @Column(name = "end_date")
    private OffsetDateTime endDate;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "start_date")
    private OffsetDateTime startDate;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "company_type")
    @Enumerated(EnumType.STRING)
    private CampaignType campaignType;

    @Column(name = "company_status")
    @Enumerated(EnumType.STRING)
    private CompanyStatus status;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "channel_id")
    private UUID channelId;

    @Column(name = "is_archived")
    private Boolean isArchived = false;

    @Column(name = "max_retargeted")
    private Long maxRetargeted;

    @OneToMany(
            mappedBy = "campaign",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}
    )
    private Set<CampaignCreative> campaignCreatives;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Campaign campaign = (Campaign) o;
        return Objects.equals(id, campaign.id) && Objects.equals(title, campaign.title) && Objects.equals(endDate, campaign.endDate) && Objects.equals(startDate, campaign.startDate) && status == campaign.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, endDate, startDate, status);
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": \"" + getId() + "\"," +
                "\"endDate\": \"" + getEndDate() + "\"," +
                "\"createdAt\": \"" + getCreatedAt() + "\"," +
                "\"startDate\": \"" + getStartDate() + "\"," +
                "\"table_name\": \"" + getTitle() + "\"," +
                "\"company_status\": \"" + getStatus() + "\"" +
                ",\"maxRetargeted\": \"" + getMaxRetargeted() + "\"" +
                "}";
    }
}
