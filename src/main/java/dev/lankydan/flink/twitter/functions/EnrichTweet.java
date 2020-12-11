package dev.lankydan.flink.twitter.functions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lankydan.flink.twitter.client.TwitterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;

import java.util.Collections;

public class EnrichTweet extends RichAsyncFunction<String, String> {

    private transient TwitterClient client;
    private transient ObjectMapper mapper;

    @Override
    public void open(Configuration parameters) throws Exception {
        client = TwitterClient.create();
        mapper = new ObjectMapper();
    }

    @Override
    public void asyncInvoke(String input, ResultFuture<String> resultFuture) throws JsonProcessingException {
        long id = mapper.readTree(input).get("id").asLong();
        client.enrich(id)
            .thenAccept(result -> resultFuture.complete(Collections.singleton(result)));
    }
}
