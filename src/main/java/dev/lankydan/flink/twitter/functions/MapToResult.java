package dev.lankydan.flink.twitter.functions;

import dev.lankydan.flink.twitter.data.Result;
import dev.lankydan.flink.twitter.data.TweetWithMentions;
import dev.lankydan.flink.twitter.json.EnrichedTweet;
import dev.lankydan.flink.twitter.json.EnrichedTweetData;
import dev.lankydan.flink.twitter.json.Mention;
import dev.lankydan.flink.twitter.json.User;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MapToResult implements MapFunction<Tuple2<TweetWithMentions, EnrichedTweet>, Result> {

    @Override
    public Result map(Tuple2<TweetWithMentions, EnrichedTweet> value) {
        EnrichedTweetData streamedTweetData = value.f0.getTweet().getSingleData();
        EnrichedTweet authorTweets = value.f1;

        Result.Tweet tweet = new Result.Tweet(
            value.f0.getMentions(),
            streamedTweetData.getPublicMetrics().getRetweetCount(),
            streamedTweetData.getPublicMetrics().getReplyCount(),
            streamedTweetData.getPublicMetrics().getLikeCount(),
            streamedTweetData.getPublicMetrics().getQuoteCount(),
            streamedTweetData.getCreatedAt(),
            streamedTweetData.getText()
        );

        List<Result.Tweet> tweets = authorTweets.getData().stream().map(data -> {
            Set<String> mentions;
            if (data.getEntities() != null && data.getEntities().getMentions() != null) {
                mentions = data.getEntities().getMentions().stream().map(Mention::getUsername).collect(Collectors.toSet());
            } else {
                mentions = Collections.emptySet();
            }
            return new Result.Tweet(
                mentions,
                data.getPublicMetrics().getRetweetCount(),
                data.getPublicMetrics().getReplyCount(),
                data.getPublicMetrics().getLikeCount(),
                data.getPublicMetrics().getQuoteCount(),
                data.getCreatedAt(),
                data.getText()
            );
        }).collect(Collectors.toList());

        tweets.add(tweet);

        User user = value.f0.getTweet().getIncludes()
            .getUsers()
            .stream()
            .filter(u -> u.getId().equals(streamedTweetData.getAuthorId()))
            .findFirst().get();

        return new Result(streamedTweetData.getAuthorId(), user.getName(), user.getUsername(), tweets);
    }
}
