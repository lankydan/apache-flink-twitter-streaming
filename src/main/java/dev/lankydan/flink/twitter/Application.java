package dev.lankydan.flink.twitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.lankydan.flink.twitter.client.TwitterClient;
import dev.lankydan.flink.twitter.data.Result;
import dev.lankydan.flink.twitter.json.EnrichedTweet;
import dev.lankydan.flink.twitter.json.EnrichedTweetData;
import dev.lankydan.flink.twitter.json.Entities;
import dev.lankydan.flink.twitter.json.Mention;
import dev.lankydan.flink.twitter.json.Tweet;
import dev.lankydan.flink.twitter.source.TwitterSourceCreator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    public static class ConvertJsonIntoTweet extends RichMapFunction<String, Tweet> {

        private transient ObjectMapper mapper;

        @Override
        public void open(Configuration parameters) {
            mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
        }

        @Override
        public Tweet map(String value) throws Exception {
            return mapper.readValue(value, Tweet.class);
        }
    }

    public static class EnrichTweet extends RichAsyncFunction<Tweet, String> {

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

    public static class ConvertJsonIntoEnrichedTweet extends RichMapFunction<String, EnrichedTweet> {

        private transient ObjectMapper mapper;
        private transient Logger log;

        @Override
        public void open(Configuration parameters) {
            mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            log = LoggerFactory.getLogger(ConvertJsonIntoEnrichedTweet.class);
        }

        @Override
        public EnrichedTweet map(String value) throws Exception {
            if (value.contains("Rate limit exceeded")) {
                log.info("RATE LIMITED");
                return null;
            }
//            JsonNode node = mapper.readValue(value, JsonNode.class);
//            return mapper.treeToValue(node.withArray("data").get(0), EnrichedTweet.class);
            return mapper.readValue(value, EnrichedTweet.class);
        }
    }

    public static class GetRecentAuthorTweets extends RichAsyncFunction<EnrichedTweet, Tuple2<EnrichedTweet, EnrichedTweet>> {

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

    public static class FilterByRepeatedMentions extends RichFilterFunction<Tuple2<EnrichedTweet, EnrichedTweet>> {

        private transient Logger log;

        @Override
        public void open(Configuration parameters) {
            log = LoggerFactory.getLogger(ConvertJsonIntoEnrichedTweet.class);
        }

        @Override
        public boolean filter(Tuple2<EnrichedTweet, EnrichedTweet> value) {
            EnrichedTweetData streamedTweetData = value.f0.getData().get(0);
            EnrichedTweet authorTweets = value.f1;

            try {
                Entities entities = streamedTweetData.getEntities();
                Set<String> streamedTweetMentions;
                if (entities != null && entities.getMentions() != null) {

                    streamedTweetMentions = entities
                        .getMentions()
                        .stream()
                        .map(Mention::getUsername)
                        .collect(Collectors.toSet());
                } else {
                    streamedTweetMentions = Collections.emptySet();
                }

                if (authorTweets.getData() == null) {
                    log.warn("Author tweets was null, {}", authorTweets.getData());
                    return false;
                }

                Set<String> authorTweetMentions = authorTweets.getData().stream()
                    .filter(data -> !data.getId().equals(streamedTweetData.getId()))
                    .map(data -> {
                        if (data.getEntities() != null && data.getEntities().getMentions() != null) {
                            return data.getEntities().getMentions();
                        } else {
                            return Collections.<Mention>emptyList();
                        }
                    })
                    .flatMap(Collection::stream)
                    .map(Mention::getUsername)
                    .collect(Collectors.toSet());

                return CollectionUtils.containsAny(streamedTweetMentions, authorTweetMentions);
            } catch (NullPointerException e) {
                throw e;
            }
        }
    }

    public static class MapToResult extends RichMapFunction<Tuple2<EnrichedTweet, EnrichedTweet>, Result> {

        @Override
        public Result map(Tuple2<EnrichedTweet, EnrichedTweet> value) {
            EnrichedTweetData streamedTweetData = value.f0.getData().get(0);
            EnrichedTweet authorTweets = value.f1;

            List<String> streamedTweetMentions;
            if (streamedTweetData.getEntities() != null && streamedTweetData.getEntities().getMentions() != null) {
                streamedTweetMentions =
                    streamedTweetData.getEntities().getMentions().stream().map(Mention::getUsername).collect(Collectors.toList());
            } else {
                streamedTweetMentions = Collections.emptyList();
            }
            Result.Tweet tweet = new Result.Tweet(
                streamedTweetMentions,
                streamedTweetData.getPublicMetrics().getRetweetCount(),
                streamedTweetData.getPublicMetrics().getReplyCount(),
                streamedTweetData.getPublicMetrics().getLikeCount(),
                streamedTweetData.getPublicMetrics().getQuoteCount(),
                streamedTweetData.getCreatedAt(),
                streamedTweetData.getText()
            );

            List<Result.Tweet> tweets = authorTweets.getData().stream().map(data -> {
                List<String> mentions;
                if (data.getEntities() != null && data.getEntities().getMentions() != null) {
                    mentions = data.getEntities().getMentions().stream().map(Mention::getUsername).collect(Collectors.toList());
                } else {
                    mentions = Collections.emptyList();
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

            return new Result(streamedTweetData.getAuthorId(), tweets);
        }
    }
}
