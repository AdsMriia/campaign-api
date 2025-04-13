package com.example.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "media")
@Getter
@Setter
public class Media extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "file_name", nullable = false)
    private UUID fileName;

    @Column(name = "file_extension", nullable = false)
    private String fileExtension;
}
