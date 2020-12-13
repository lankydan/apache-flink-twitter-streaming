package dev.lankydan.flink.twitter;

import dev.lankydan.flink.twitter.data.Result;
import dev.lankydan.flink.twitter.data.TweetWithMentions;
import dev.lankydan.flink.twitter.functions.ConvertJsonIntoEnrichedTweet;
import dev.lankydan.flink.twitter.functions.EnrichTweet;
import dev.lankydan.flink.twitter.functions.ExtractMentionsFromTweet;
import dev.lankydan.flink.twitter.functions.FilterByNewTweets;
import dev.lankydan.flink.twitter.functions.FilterByRepeatedMentions;
import dev.lankydan.flink.twitter.functions.GetRecentAuthorTweets;
import dev.lankydan.flink.twitter.functions.MapToResult;
import dev.lankydan.flink.twitter.json.EnrichedTweetData;
import dev.lankydan.flink.twitter.source.TwitterSourceCreator;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Application {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment environment = StreamExecutionEnvironment
            .getExecutionEnvironment();

        DataStream<String> stream = environment.addSource(TwitterSourceCreator.create())
            .filter(new FilterByNewTweets());

        DataStream<TweetWithMentions> enriched =
            AsyncDataStream.unorderedWait(stream, new EnrichTweet(), 5000, TimeUnit.MILLISECONDS)
                .map(new ConvertJsonIntoEnrichedTweet())
                // Ignore rate limit errors
                .filter(Objects::nonNull)
                // some enriched tweets don't have data or authors, not sure why but filter them out anyway
                .filter(tweet -> {
                    EnrichedTweetData data = tweet.getSingleData();
                    return data != null && data.getAuthorId() != null;
                })
                .map(new ExtractMentionsFromTweet());

        DataStream<Result> results =
            AsyncDataStream.unorderedWait(enriched, new GetRecentAuthorTweets(), 5000, TimeUnit.MILLISECONDS)
                .filter(new FilterByRepeatedMentions())
                .map(new MapToResult());

        results.print();
        environment.execute();
    }
}
