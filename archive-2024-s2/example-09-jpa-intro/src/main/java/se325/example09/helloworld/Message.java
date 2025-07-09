package se325.example09.helloworld;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Message {

    @Id
    @GeneratedValue
    private Long id;

    private String content;
    private LocalDateTime creationTime;

    @ElementCollection
    private List<Comment> comments = new ArrayList<>();

    public Message() {
    }

    public Message(String content) {
        this(content, LocalDateTime.now());
    }

    public Message(String content, LocalDateTime creationTime) {
        this.content = content;
        this.creationTime = creationTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public List<Comment> getComments() {
        return comments;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", creationTime=" + creationTime +
                '}';
    }
}
