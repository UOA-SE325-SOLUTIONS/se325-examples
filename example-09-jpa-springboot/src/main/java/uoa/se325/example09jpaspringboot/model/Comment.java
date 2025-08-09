package uoa.se325.example09jpaspringboot.model;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class Comment {

    private String text;
    private LocalDateTime timestamp;

    public Comment() {
    }

    public Comment(String text, LocalDateTime timestamp) {
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return text.equals(comment.text) && timestamp.equals(comment.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, timestamp);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
