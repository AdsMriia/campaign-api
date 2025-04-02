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
@Table(name = "medias")
public class Media implements Cloneable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "file_name")
    private UUID fileName;

    @Column(name = "file_extension")
    private String fileExtension;

    @Override
    public String toString() {
        return "{" +
                "\"id\": \"" + getId() + "\"," +
                "\"fileName\": \"" + getFileName() + "\"," +
                "\"fileExtension\": \"" + getFileExtension() + "\"" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Media media = (Media) o;
        return Objects.equals(id, media.id) && Objects.equals(fileName, media.fileName);
    }

    @Override
    public Media clone() {
        Media media = new Media();
        media.setWorkspaceId(workspaceId);
        media.setFileName(fileName);
        media.setFileExtension(fileExtension);
        return media;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName);
    }
}
