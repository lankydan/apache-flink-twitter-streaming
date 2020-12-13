package dev.lankydan.flink.twitter.functions;

import dev.lankydan.flink.twitter.data.TweetWithMentions;
import dev.lankydan.flink.twitter.json.EnrichedTweet;
import dev.lankydan.flink.twitter.json.EnrichedTweetData;
import dev.lankydan.flink.twitter.json.Mention;
import org.apache.flink.api.common.functions.MapFunction;

import java.util.Set;
import java.util.stream.Collectors;

public class ExtractMentionsFromTweet implements MapFunction<EnrichedTweet, TweetWithMentions> {

    @Override
    public TweetWithMentions map(EnrichedTweet value) {
        EnrichedTweetData data = value.getSingleData();
        if (data.getEntities() == null || data.getEntities().getMentions() == null) {
            return new TweetWithMentions(value, Set.of());
        }
        return new TweetWithMentions(
            value,
            data.getEntities().getMentions().stream().map(Mention::getUsername).collect(Collectors.toSet())
        );
    }
}
