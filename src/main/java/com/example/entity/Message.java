package com.example.entity;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.example.model.MessageStatus;
import com.example.model.MessageType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "messages")
public class Message extends BaseEntity {

    @Column(name = "mark_down", nullable = false)
    private Boolean markDown;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @Column(name = "telegram_id")
    private Long telegramId;

    @Column(nullable = false)
    private String text;

    @Column(name = "channel_id")
    private UUID channelId;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MediaToMessage> medias = new HashSet<>();

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Action> actions = new HashSet<>();

    @OneToMany(mappedBy = "message")
    private Set<CampaignCreative> campaigns = new HashSet<>();

//    public void addMedia(Media media) {
//        medias.add(media);
//        media.setMessage(this);
//    }

//    public void removeMedia(Media media) {
//        medias.remove(media);
//        media.setMessage(null);
//    }

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
                + "\"id\": \"" + getId() + "\","
                + "\"markDown\": " + markDown + ","
                + "\"title\": \"" + title + "\","
                + "\"type\": \"" + type + "\","
                + "\"status\": \"" + status + "\","
                + "\"createdAt\": \"" + getCreatedAt() + "\","
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
        return Objects.equals(getId(), message.getId()) && Objects.equals(telegramId, message.telegramId);
    }

    @Override
    public Message clone() {
        Message message = new Message();
        message.setMarkDown(this.markDown);
        message.setTitle(this.title);
        message.setType(this.type);
        message.setStatus(MessageStatus.DRAFT);
        message.setText(this.text);
        message.setCreatedBy(this.getCreatedBy());
        message.setChannelId(this.channelId);
        message.setWorkspaceId(this.workspaceId);
        message.setCreatedAt(OffsetDateTime.now());
        message.setUpdatedAt(OffsetDateTime.now());

        for (Action action : this.actions) {
            Action newAction = new Action();
            newAction.setMessage(message);
            newAction.setText(action.getText());
            newAction.setLink(action.getLink());
            newAction.setOrdinal(action.getOrdinal());
            message.getActions().add(newAction);
        }

        for (MediaToMessage mediaToMessage : this.medias) {
            MediaToMessage newMediaToMessage = new MediaToMessage();
            newMediaToMessage.setMessage(message);
            newMediaToMessage.setMedia(mediaToMessage.getMedia());
            message.getMedias().add(newMediaToMessage);
        }

        return message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), telegramId);
    }
}
