package dev.lankydan.flink.twitter.data;

import dev.lankydan.flink.twitter.json.EnrichedTweetData;

import java.util.Set;

public class StreamedTweet {

    private EnrichedTweetData tweet;
    private String name;
    private String username;
    private Set<String> mentions;

    public StreamedTweet() {
    }

    public StreamedTweet(EnrichedTweetData tweet, String name, String username, Set<String> mentions) {
        this.tweet = tweet;
        this.name = name;
        this.username = username;
        this.mentions = mentions;
    }

    public EnrichedTweetData getTweet() {
        return tweet;
    }

    public void setTweet(EnrichedTweetData tweet) {
        this.tweet = tweet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getMentions() {
        return mentions;
    }

    public void setMentions(Set<String> mentions) {
        this.mentions = mentions;
    }
}
