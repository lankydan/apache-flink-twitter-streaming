package dev.lankydan.flink.twitter.functions;

import dev.lankydan.flink.twitter.data.RecentTweet;
import dev.lankydan.flink.twitter.data.StreamedTweet;
import dev.lankydan.flink.twitter.json.EnrichedTweet;
import dev.lankydan.flink.twitter.json.EnrichedTweetData;
import dev.lankydan.flink.twitter.json.Mention;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MapToRecentTweets implements MapFunction<Tuple2<StreamedTweet, EnrichedTweet>, Tuple2<StreamedTweet, List<RecentTweet>>> {

    @Override
    public Tuple2<StreamedTweet, List<RecentTweet>> map(Tuple2<StreamedTweet, EnrichedTweet> value) {
        List<EnrichedTweetData> recentTweetData = value.f1.getData();

        // filters out recent results that include no tweets
        if (recentTweetData == null) {
            return new Tuple2<>(value.f0, Collections.emptyList());
        }

        return Tuple2.of(
            value.f0,
            value.f1.getData().stream().map(this::toRecentTweet).collect(Collectors.toList())
        );
    }

    private RecentTweet toRecentTweet(EnrichedTweetData data) {
        if (data.getEntities() == null || data.getEntities().getMentions() == null) {
            return new RecentTweet(data, Set.of());
        }
        return new RecentTweet(
            data,
            data.getEntities().getMentions().stream().map(Mention::getUsername).collect(Collectors.toSet())
        );
    }
}
