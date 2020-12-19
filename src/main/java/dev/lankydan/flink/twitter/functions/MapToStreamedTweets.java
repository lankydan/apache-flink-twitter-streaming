package dev.lankydan.flink.twitter.functions;

import dev.lankydan.flink.twitter.data.StreamedTweet;
import dev.lankydan.flink.twitter.json.EnrichedTweet;
import dev.lankydan.flink.twitter.json.EnrichedTweetData;
import dev.lankydan.flink.twitter.json.Mention;
import dev.lankydan.flink.twitter.json.User;
import org.apache.flink.api.common.functions.MapFunction;

import java.util.Set;
import java.util.stream.Collectors;

public class MapToStreamedTweets implements MapFunction<EnrichedTweet, StreamedTweet> {

    @Override
    public StreamedTweet map(EnrichedTweet value) {
        EnrichedTweetData data = value.getSingleData();
        User user = getUser(value);
        if (data.getEntities() == null || data.getEntities().getMentions() == null) {
            return new StreamedTweet(data, user.getName(), user.getUsername(), Set.of());
        }
        return new StreamedTweet(
            data,
            user.getName(),
            user.getUsername(),
            data.getEntities().getMentions().stream().map(Mention::getUsername).collect(Collectors.toSet())
        );
    }

    private User getUser(EnrichedTweet value) {
        return value.getIncludes()
            .getUsers()
            .stream()
            .filter(u -> u.getId().equals(value.getSingleData().getAuthorId()))
            .findFirst()
            .get();
    }
}
