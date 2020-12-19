package dev.lankydan.flink.twitter.data;

import dev.lankydan.flink.twitter.json.EnrichedTweetData;

import java.util.Set;

public class RecentTweet {

    private EnrichedTweetData tweet;
    private Set<String> mentions;

    public RecentTweet(EnrichedTweetData tweet, Set<String> mentions) {
        this.tweet = tweet;
        this.mentions = mentions;
    }

    public EnrichedTweetData getTweet() {
        return tweet;
    }

    public Set<String> getMentions() {
        return mentions;
    }
}
