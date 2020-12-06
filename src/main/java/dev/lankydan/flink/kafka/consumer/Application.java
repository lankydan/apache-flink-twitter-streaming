package dev.lankydan.flink.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Application {

    // Kafka version
    /*public static void main(String[] args) throws Exception {
        String inputTopic = "flink_input";
        String outputTopic = "flink_output";
        // this group id is unique for consumers and don't need to find some property from a kafka instance
        String consumerGroup = "dan";
        String address = "localhost:9092";
        StreamExecutionEnvironment environment = StreamExecutionEnvironment
            .getExecutionEnvironment();
        FlinkKafkaConsumer<String> flinkKafkaConsumer = Consumer.createStringConsumerForTopic(
            inputTopic,
            address,
            consumerGroup
        );

        DataStream<String> stringInputStream = environment.addSource(flinkKafkaConsumer);

        stringInputStream.filter(value -> value.startsWith("asd")).print();

        environment.execute();
    }*/

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment
            .getExecutionEnvironment();
        DataStream<String> stream = environment.addSource(TwitterSourceCreator.create());
        DataStream<Tweet> tweetDataStream = stream
            // probably no point having this conversion anymore because of the enrichment that comes after
            .map(new ConvertJsonIntoTweet())
            // lazy filtering to include new tweets
            .filter(tweet -> tweet.getCreatedAt() != null)
            /*.filter(tweet -> tweet.getText().contains("Trump"))*/;

//        KeyedStream<EnrichedTweet, String> enriched =
        DataStream<EnrichedTweet> enriched =
            AsyncDataStream.unorderedWait(tweetDataStream, new EnrichTweet(), 5000, TimeUnit.MILLISECONDS)
                .map(new ConvertJsonIntoEnrichedTweet())
                // Ignore rate limit errors
                .filter(Objects::nonNull);
//                .keyBy(tweet -> tweet.getData().get(0).getAuthorId());

        DataStream<Tuple2<EnrichedTweet, EnrichedTweet>> withAuthorTweets =
            AsyncDataStream.unorderedWait(enriched, new GetRecentAuthorTweets(), 5000, TimeUnit.MILLISECONDS)
            .filter(new FilterByRepeatedMentions());

        withAuthorTweets.print();
        environment.execute();
    }

    // don't think I can filter by user because I am only given a subset of all tweets
    // therefore the chance of getting tweets by the same person is highly unlikely

    // could do something that filters by hashtags or mentions

    // then would it be possible to pull information about the user to se how many times
    // they tweeted using that hashtag/mention

    public static class ConvertJsonIntoTweet extends RichMapFunction<String, Tweet> {

        private transient ObjectMapper mapper;

        @Override
        public void open(Configuration parameters) {
            mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
        }

        @Override
        public Tweet map(String value) throws Exception {
            // log something if rate limited but continue processing
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

        @Override
        public void open(Configuration parameters) throws Exception {
            mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            client = TwitterClient.create();
        }

        @Override
        public void asyncInvoke(EnrichedTweet input, ResultFuture<Tuple2<EnrichedTweet, EnrichedTweet>> resultFuture) {
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
        public boolean filter(Tuple2<EnrichedTweet, EnrichedTweet> value) throws Exception {
            EnrichedTweet streamedTweet = value.f0;
            EnrichedTweet authorTweets = value.f1;

            if (streamedTweet.getData() == null) {
                log.warn("Streamed tweet was null, {}", streamedTweet);
                return false;
            }

            Set<String> streamedTweetMentions = streamedTweet.getData().get(0)
                .getEntities()
                .getMentions()
                .stream()
                .map(Mention::getUsername)
                .collect(Collectors.toSet());

            if (authorTweets.getData() == null) {
                log.warn("Author tweets was null, {}", streamedTweet);
                return false;
            }

            Set<String> authorTweetMentions = authorTweets.getData().stream()
                .filter(data -> !data.getId().equals(streamedTweet.getData().get(0).getId()))
                .map(data -> data.getEntities().getMentions())
                .flatMap(Collection::stream)
                .map(Mention::getUsername)
                .collect(Collectors.toSet());

            return CollectionUtils.containsAny(streamedTweetMentions, authorTweetMentions);
        }
    }

//    public static class KeyByUser extends Rich<String> {
//
//        private transient ObjectMapper mapper;
//
//        @Override
//        public void open(Configuration parameters) {
//            mapper = new ObjectMapper();
//            mapper.registerModule(new JavaTimeModule());
//        }
//
//
//        @Override
//        public boolean filter(String value) throws Exception {
//            JsonNode json =  mapper.readValue(value, JsonNode.class);
//            json.get("author_id");
//        }
//    }
}
