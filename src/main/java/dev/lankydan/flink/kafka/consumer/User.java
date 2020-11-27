package dev.lankydan.flink.kafka.consumer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    private String id;
    private String username;
    private String name;
    @JsonProperty("public_metrics")
    private UserPublicMetrics publicMetrics;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserPublicMetrics getPublicMetrics() {
        return publicMetrics;
    }

    public void setPublicMetrics(UserPublicMetrics publicMetrics) {
        this.publicMetrics = publicMetrics;
    }

    @Override
    public String toString() {
        return "User{" +
            "id='" + id + '\'' +
            ", username='" + username + '\'' +
            ", name='" + name + '\'' +
            ", publicMetrics=" + publicMetrics +
            '}';
    }
}
