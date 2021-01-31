package dev.lankydan.flink.twitter.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class Result {

    private String authorId;
    private String name;
    private String username;
    private List<Tweet> tweets;

    public Result() {
    }

    public Result(String authorId, String name, String username, List<Tweet> tweets) {
        this.authorId = authorId;
        this.name = name;
        this.username = username;
        this.tweets = tweets;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
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

    public List<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    @Override
    public String toString() {
        return "========\n" +
            "Result{" +
            "authorId='" + authorId + '\'' +
            "name='" + name + '\'' +
            "username='" + username + '\'' +
            ",\n  tweets=" + tweets +
            "\n}\n" +
            "========\n";
    }

    public static class Tweet {
        private Set<String> mentions;
        private int retweetCount;
        private int replyCount;
        private int likeCount;
        private int quoteCount;
        private LocalDateTime createdAt;

        private String text;

        public Tweet(Set<String> mentions, int retweetCount, int replyCount, int likeCount, int quoteCount, LocalDateTime createdAt, String text) {
            this.mentions = mentions;
            this.retweetCount = retweetCount;
            this.replyCount = replyCount;
            this.likeCount = likeCount;
            this.quoteCount = quoteCount;
            this.createdAt = createdAt;
            this.text = text;
        }

        public Set<String> getMentions() {
            return mentions;
        }

        public void setMentions(Set<String> mentions) {
            this.mentions = mentions;
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

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "Tweet{" +
                "mentions=" + mentions +
                ", retweetCount=" + retweetCount +
                ", replyCount=" + replyCount +
                ", likeCount=" + likeCount +
                ", quoteCount=" + quoteCount +
                ", createdAt=" + createdAt +
                ", text='" + text + '\'' +
                "}\n";
        }
    }
}
