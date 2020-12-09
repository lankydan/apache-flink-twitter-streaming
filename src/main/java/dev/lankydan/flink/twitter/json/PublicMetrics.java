package dev.lankydan.flink.twitter.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PublicMetrics {

    @JsonProperty("retweet_count")
    private int retweetCount;
    @JsonProperty("reply_count")
    private int replyCount;
    @JsonProperty("like_count")
    private int likeCount;
    @JsonProperty("quote_count")
    private int quoteCount;

    public PublicMetrics() {
    }

    public PublicMetrics(int retweetCount, int replyCount, int likeCount, int quoteCount) {
        this.retweetCount = retweetCount;
        this.replyCount = replyCount;
        this.likeCount = likeCount;
        this.quoteCount = quoteCount;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getQuoteCount() {
        return quoteCount;
    }

    public void setQuoteCount(int quoteCount) {
        this.quoteCount = quoteCount;
    }

    @Override
    public String toString() {
        return "PublicMetrics{" +
            "retweetCount=" + retweetCount +
            ", replyCount=" + replyCount +
            ", likeCount=" + likeCount +
            ", quoteCount=" + quoteCount +
            '}';
    }
}
