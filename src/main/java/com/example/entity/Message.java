package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.enums.MessageStatus;
import org.example.entity.enums.MessageType;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "obj_pools")
public class Message implements Cloneable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "mark_down")
    private Boolean markDown;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "title")
    private String title;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @Column(name = "telegram_id")
    private Integer telegramId;

    @Column(name = "text")
    private String text;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "channel_id")
    private UUID channelId;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToMany(
            mappedBy = "objectPool",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY
    )
    Set<Action> actions;

    @OneToMany(
            mappedBy = "objectPool",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY
    )
    Set<Media> medias;

    public void addMedia(Media file) {
        if (medias == null) {
            medias = new HashSet<>();
        }
        file.setMessage(this);
        medias.add(file);
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": \"" + id + "\"," +
                "\"markDown\": " + markDown + "," +
                "\"title\": " + title + "," +
                "\"type\": \"" + type + "\"," +
                "\"status\": \"" + status + "\"," +
                "\"createdAt\": \"" + createdAt + "\"," +
                "\"text\": \"" + text + "\"," +
                "\"telegramId\": " + telegramId +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message objectPool = (Message) o;
        return Objects.equals(id, objectPool.id) && Objects.equals(telegramId, objectPool.telegramId);
    }

    @Override
    public Message clone() {
        Message message = new Message();
        message.setWorkspace(workspace);
        message.setType(type);
        message.setStatus(status);
        message.setTelegramId(telegramId);
        message.setText(text);
        message.setCreatedBy(createdBy);
        message.setChannel(channel);
        message.setCreatedAt(OffsetDateTime.now());
        message.setUpdatedAt(OffsetDateTime.now());
        message.setMarkDown(markDown);
        message.setTitle(title);
        message.setActions(actions.stream().map(Action::clone).collect(Collectors.toSet()));
        message.setMedias(medias.stream().map(Media::clone).collect(Collectors.toSet()));
        return message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, telegramId, telegramId);
    }
}
