package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "actions")
public class Action implements Cloneable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    @Column(name = "text")
    private String text;

    @Column(name = "link")
    private String link;

    @Column(name = "ordinal")
    private Integer ordinal = 0;

//    @OneToMany(
//            mappedBy = "action",
//            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
//            fetch = FetchType.LAZY
//    )
//    Set<ActionToSubscriber> actionToSubscribers;

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + "\"," +
                "\"text\":\"" + text + "\"," +
                "\"link\":\"" + link + "\"," +
                "\"ordinal\":\"" + ordinal + "\"" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return Objects.equals(id, action.id) && Objects.equals(text, action.text) && Objects.equals(link, action.link);
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
        return Objects.hash(id, text, link);
    }
}
