package com.example.entity;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.model.MessageStatus;
import com.example.model.MessageType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ab_messages")
public class Message extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "mark_down")
    private Boolean markDown = false;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MessageType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MessageStatus status;

    @Column(name = "telegram_id")
    private Integer telegramId;

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "channel_id", nullable = false)
    private UUID channelId;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Action> actions = new HashSet<>();

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Media> medias = new HashSet<>();

    public void addMedia(Media media) {
        medias.add(media);
        media.setMessage(this);
    }

    public void removeMedia(Media media) {
        medias.remove(media);
        media.setMessage(null);
    }

    public void addAction(Action action) {
        actions.add(action);
        action.setMessage(this);
    }

    public void removeAction(Action action) {
        actions.remove(action);
        action.setMessage(null);
    }

    @Override
    public String toString() {
        return "{"
                + "\"id\": \"" + id + "\","
                + "\"markDown\": " + markDown + ","
                + "\"title\": \"" + title + "\","
                + "\"type\": \"" + type + "\","
                + "\"status\": \"" + status + "\","
                + "\"createdAt\": \"" + createdAt + "\","
                + "\"text\": \"" + text + "\","
                + "\"telegramId\": " + telegramId
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return Objects.equals(id, message.id) && Objects.equals(telegramId, message.telegramId);
    }

    @Override
    public Message clone() {
        Message message = new Message();
        message.setWorkspaceId(workspaceId);
        message.setType(type);
        message.setStatus(status);
        message.setTelegramId(telegramId);
        message.setText(text);
        message.setCreatedBy(createdBy);
        message.setChannelId(channelId);
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
        return Objects.hash(id, telegramId);
    }
}
