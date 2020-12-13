package dev.lankydan.flink.twitter.functions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.lankydan.flink.twitter.client.TwitterClient;
import dev.lankydan.flink.twitter.data.TweetWithMentions;
import dev.lankydan.flink.twitter.json.EnrichedTweet;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;

import java.util.List;

public class GetRecentAuthorTweets extends RichAsyncFunction<TweetWithMentions, Tuple2<EnrichedTweet, EnrichedTweet>> {

    private transient ObjectMapper mapper;
    private transient TwitterClient client;

    @Override
    public void open(Configuration parameters) throws Exception {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        client = TwitterClient.create();
    }

    @Override
    public void asyncInvoke(TweetWithMentions input, ResultFuture<Tuple2<EnrichedTweet, EnrichedTweet>> resultFuture) {
        EnrichedTweet streamedTweet = input.getTweet();
        client.getRecentTweetsByAuthor(streamedTweet.getData().get(0).getAuthorId())
            .thenAccept(result -> {
                try {
                    EnrichedTweet tweets = mapper.readValue(result, EnrichedTweet.class);
                    resultFuture.complete(List.of(Tuple2.of(streamedTweet, tweets)));
                } catch (JsonProcessingException e) {
                    resultFuture.completeExceptionally(e);
                }
            });
    }
}
