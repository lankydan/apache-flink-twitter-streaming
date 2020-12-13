package dev.lankydan.flink.twitter.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class Result {

    private String authorId;
    private String name;
    private String username;
    private List<Tweet> tweets;

    public Result(String authorId, String name, String username, List<Tweet> tweets) {
        this.authorId = authorId;
        this.name = name;
        this.username = username;
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
