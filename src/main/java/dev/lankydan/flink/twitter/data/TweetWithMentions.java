package dev.lankydan.flink.twitter.data;

import dev.lankydan.flink.twitter.json.EnrichedTweet;

import java.util.Objects;
import java.util.Set;

public class TweetWithMentions {

    private EnrichedTweet tweet;
    private Set<String> mentions;

    public TweetWithMentions(EnrichedTweet tweet, Set<String> mentions) {
        this.tweet = tweet;
        this.mentions = mentions;
    }

    public EnrichedTweet getTweet() {
        return tweet;
    }

    public Set<String> getMentions() {
        return mentions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TweetWithMentions that = (TweetWithMentions) o;
        return Objects.equals(tweet, that.tweet) &&
            Objects.equals(mentions, that.mentions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tweet, mentions);
    }
}
