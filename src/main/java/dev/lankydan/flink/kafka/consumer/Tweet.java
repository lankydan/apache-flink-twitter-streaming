package dev.lankydan.flink.kafka.consumer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tweet {

    private Long id;
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "EE MMM dd HH:mm:ss Z yyyy")
    private LocalDateTime createdAt;
    @JsonProperty("id_str")
    private String idString;
    private String text;

    public Tweet() {
    }

    public Tweet(Long id, LocalDateTime createdAt, String idString, String text) {
        this.id = id;
        this.createdAt = createdAt;
        this.idString = idString;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tweet tweet = (Tweet) o;
        return Objects.equals(id, tweet.id) &&
            Objects.equals(createdAt, tweet.createdAt) &&
            Objects.equals(idString, tweet.idString) &&
            Objects.equals(text, tweet.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, idString, text);
    }

    @Override
    public String toString() {
        return "Tweet{" +
            "id=" + id +
            ", createdAt=" + createdAt +
            ", idString='" + idString + '\'' +
            ", text='" + text + '\'' +
            '}';
    }
}
