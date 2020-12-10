package dev.lankydan.flink.twitter.functions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.lankydan.flink.twitter.Application;
import dev.lankydan.flink.twitter.client.TwitterClient;
import dev.lankydan.flink.twitter.json.EnrichedTweet;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class GetRecentAuthorTweets extends RichAsyncFunction<EnrichedTweet, Tuple2<EnrichedTweet, EnrichedTweet>> {

    private transient ObjectMapper mapper;
    private transient TwitterClient client;
    private transient Logger log;

    @Override
    public void open(Configuration parameters) throws Exception {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        client = TwitterClient.create();
        log = LoggerFactory.getLogger(GetRecentAuthorTweets.class);
    }

    @Override
    public void asyncInvoke(EnrichedTweet input, ResultFuture<Tuple2<EnrichedTweet, EnrichedTweet>> resultFuture) {
        if (input.getData() == null) {
            log.info("Data was null: {}", input);
        } else if (input.getData().get(0) == null) {
            log.info("Data .get(0) was null: {}", input.getData());
        } else if (input.getData().get(0).getAuthorId() == null) {
            log.info("Author was null: {}", input.getData().get(0));
        }
        client.getRecentTweetsByAuthor(input.getData().get(0).getAuthorId())
            .thenAccept(result -> {
                try {
                    EnrichedTweet tweets = mapper.readValue(result, EnrichedTweet.class);
                    resultFuture.complete(Collections.singleton(Tuple2.of(input, tweets)));
                } catch (JsonProcessingException e) {
                    resultFuture.completeExceptionally(e);
                }
            });
    }
}
