package dev.lankydan.flink.twitter.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.configuration.Configuration;

/**
 * Filters the stream to only contain new tweets (tweet creation events) by removing all
 * _delete_ tweets.
 */
public class FilterByNewTweets extends RichFilterFunction<String> {

    private transient ObjectMapper mapper;

    @Override
    public void open(Configuration parameters) {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public boolean filter(String value) throws Exception {
        return !mapper.readTree(value).has("delete");
    }
}
