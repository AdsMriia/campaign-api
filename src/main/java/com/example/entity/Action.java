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
@Table(name = "actions")
public class Action extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "link")
    private String link;

    @Column(name = "ordinal", nullable = false)
    private Integer ordinal;

//    @OneToMany(
//            mappedBy = "action",
//            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
//            fetch = FetchType.LAZY
//    )
//    Set<ActionToSubscriber> actionToSubscribers;
    @Override
    public String toString() {
        return "{"
                + "\"id\":\"" + getId() + "\","
                + "\"text\":\"" + text + "\","
                + "\"link\":\"" + link + "\","
                + "\"ordinal\":\"" + ordinal + "\""
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
        Action action = (Action) o;
        return Objects.equals(getId(), action.getId()) && Objects.equals(text, action.text) && Objects.equals(link, action.link);
    }

    @Override
    protected Action clone() {
        Action action = new Action();
        action.setText(text);
        action.setLink(link);
        return action;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), text, link);
    }
}
