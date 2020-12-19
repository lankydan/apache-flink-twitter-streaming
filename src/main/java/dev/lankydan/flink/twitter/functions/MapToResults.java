package dev.lankydan.flink.twitter.functions;

import dev.lankydan.flink.twitter.data.RecentTweet;
import dev.lankydan.flink.twitter.data.Result;
import dev.lankydan.flink.twitter.data.StreamedTweet;
import dev.lankydan.flink.twitter.json.EnrichedTweetData;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.List;
import java.util.stream.Collectors;

public class MapToResults implements MapFunction<Tuple2<StreamedTweet, List<RecentTweet>>, Result> {

    @Override
    public Result map(Tuple2<StreamedTweet, List<RecentTweet>> value) {
        EnrichedTweetData streamedTweetData = value.f0.getTweet();
        List<RecentTweet> recentTweets = value.f1;

        Result.Tweet streamedTweetResult = new Result.Tweet(
            value.f0.getMentions(),
            streamedTweetData.getPublicMetrics().getRetweetCount(),
            streamedTweetData.getPublicMetrics().getReplyCount(),
            streamedTweetData.getPublicMetrics().getLikeCount(),
            streamedTweetData.getPublicMetrics().getQuoteCount(),
            streamedTweetData.getCreatedAt(),
            streamedTweetData.getText()
        );

        List<Result.Tweet> tweets = recentTweets.stream().map(tweet -> {
            EnrichedTweetData data = tweet.getTweet();
            return new Result.Tweet(
                tweet.getMentions(),
                data.getPublicMetrics().getRetweetCount(),
                data.getPublicMetrics().getReplyCount(),
                data.getPublicMetrics().getLikeCount(),
                data.getPublicMetrics().getQuoteCount(),
                data.getCreatedAt(),
                data.getText()
            );
        }).collect(Collectors.toList());

        tweets.add(streamedTweetResult);

        return new Result(streamedTweetData.getAuthorId(), value.f0.getName(), value.f0.getUsername(), tweets);
    }
}
