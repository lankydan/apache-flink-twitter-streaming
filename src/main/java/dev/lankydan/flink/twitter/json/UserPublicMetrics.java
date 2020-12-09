package dev.lankydan.flink.twitter.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPublicMetrics {

    @JsonProperty("followers_count")
    private int followerCount;
    @JsonProperty("following_count")
    private int followingCount;
    @JsonProperty("tweet_count")
    private int tweetCount;
    @JsonProperty("listed_count")
    private int listedCount;

    public UserPublicMetrics() {
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getTweetCount() {
        return tweetCount;
    }

    public void setTweetCount(int tweetCount) {
        this.tweetCount = tweetCount;
    }

    public int getListedCount() {
        return listedCount;
    }

    public void setListedCount(int listedCount) {
        this.listedCount = listedCount;
    }

    @Override
    public String toString() {
        return "UserPublicMetrics{" +
            "followerCount=" + followerCount +
            ", followingCount=" + followingCount +
            ", tweetCount=" + tweetCount +
            ", listedCount=" + listedCount +
            '}';
    }
}
