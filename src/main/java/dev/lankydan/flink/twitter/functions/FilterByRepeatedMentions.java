package dev.lankydan.flink.twitter.functions;

import dev.lankydan.flink.twitter.json.EnrichedTweet;
import dev.lankydan.flink.twitter.json.EnrichedTweetData;
import dev.lankydan.flink.twitter.json.Entities;
import dev.lankydan.flink.twitter.json.Mention;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterByRepeatedMentions extends RichFilterFunction<Tuple2<EnrichedTweet, EnrichedTweet>> {

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
                        return List.<Mention>of();
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
