package dev.lankydan.flink.twitter.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Mention {

    private String username;

    public Mention() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Mention{" +
            "username='" + username + '\'' +
            '}';
    }
}
