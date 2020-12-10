package dev.lankydan.flink.twitter.functions;

import dev.lankydan.flink.twitter.client.TwitterClient;
import dev.lankydan.flink.twitter.json.Tweet;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;

import java.util.Collections;

public class EnrichTweet extends RichAsyncFunction<Tweet, String> {

    private transient TwitterClient client;

    @Override
    public void open(Configuration parameters) throws Exception {
        client = TwitterClient.create();
    }

    @Override
    public void asyncInvoke(Tweet input, ResultFuture<String> resultFuture) {
        client.enrich(input.getIdString())
            .thenAccept(result -> resultFuture.complete(Collections.singleton(result)));
    }
}
