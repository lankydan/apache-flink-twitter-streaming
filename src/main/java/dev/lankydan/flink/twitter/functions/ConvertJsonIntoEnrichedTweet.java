package dev.lankydan.flink.twitter.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.lankydan.flink.twitter.json.EnrichedTweet;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConvertJsonIntoEnrichedTweet extends RichMapFunction<String, EnrichedTweet> {

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
        return mapper.readValue(value, EnrichedTweet.class);
    }
}
