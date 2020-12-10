package dev.lankydan.flink.twitter;

import dev.lankydan.flink.twitter.data.Result;
import dev.lankydan.flink.twitter.functions.ConvertJsonIntoEnrichedTweet;
import dev.lankydan.flink.twitter.functions.ConvertJsonIntoTweet;
import dev.lankydan.flink.twitter.functions.EnrichTweet;
import dev.lankydan.flink.twitter.functions.FilterByRepeatedMentions;
import dev.lankydan.flink.twitter.functions.GetRecentAuthorTweets;
import dev.lankydan.flink.twitter.functions.MapToResult;
import dev.lankydan.flink.twitter.json.EnrichedTweet;
import dev.lankydan.flink.twitter.json.Tweet;
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

        DataStream<String> stream = environment.addSource(TwitterSourceCreator.create());

        DataStream<Tweet> tweetDataStream = stream
            // probably no point having this conversion anymore because of the enrichment that comes after
            .map(new ConvertJsonIntoTweet())
            // lazy filtering to include new tweets
            .filter(tweet -> tweet.getCreatedAt() != null);

        DataStream<EnrichedTweet> enriched =
            AsyncDataStream.unorderedWait(tweetDataStream, new EnrichTweet(), 5000, TimeUnit.MILLISECONDS)
                .map(new ConvertJsonIntoEnrichedTweet())
                // Ignore rate limit errors
                .filter(Objects::nonNull)
                // some enriched tweets don't have data, not sure why but filter them out anyway
                .filter(tweet -> tweet.getData() != null)
                // some enriched tweets don't have authors, not sure why but filter them out anyway
                .filter(tweet -> tweet.getData().get(0).getAuthorId() != null);

        DataStream<Result> results =
            AsyncDataStream.unorderedWait(enriched, new GetRecentAuthorTweets(), 5000, TimeUnit.MILLISECONDS)
                .filter(new FilterByRepeatedMentions())
                .map(new MapToResult());

        results.print();
        environment.execute();
    }
}
