package dev.lankydan.flink.twitter.data;

import java.util.List;

public class Result {

    private String authorId;
    private List<Tweet> tweets;

    public Result(String authorId, List<Tweet> tweets) {
        this.authorId = authorId;
        this.tweets = tweets;
    }

    @Override
    public String toString() {
        return "========\n" +
            "Result{" +
            "authorId='" + authorId + '\'' +
            ",\n  tweets=" + tweets +
            "\n}\n" +
            "========\n";
    }

    static class Tweet {
        private List<String> mentions;
        private int retweetCount;
        private int replyCount;
        private int likeCount;
        private int quoteCount;
        private String text;

        public Tweet(List<String> mentions, int retweetCount, int replyCount, int likeCount, int quoteCount, String text) {
            this.mentions = mentions;
            this.retweetCount = retweetCount;
            this.replyCount = replyCount;
            this.likeCount = likeCount;
            this.quoteCount = quoteCount;
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
                ", text='" + text + '\'' +
                "}\n";
        }
    }
}
