package dev.lankydan.flink.twitter.functions;

import dev.lankydan.flink.twitter.data.RecentTweet;
import dev.lankydan.flink.twitter.data.StreamedTweet;
import dev.lankydan.flink.twitter.json.EnrichedTweetData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterByRepeatedMentions implements FilterFunction<Tuple2<StreamedTweet, List<RecentTweet>>> {

    @Override
    public boolean filter(Tuple2<StreamedTweet, List<RecentTweet>> value) {
        EnrichedTweetData streamedTweetData = value.f0.getTweet();
        List<RecentTweet> recentTweets = value.f1;

        Set<String> authorTweetMentions = recentTweets.stream()
            .filter(tweet -> !tweet.getTweet().getId().equals(streamedTweetData.getId()))
            .map(RecentTweet::getMentions)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());

        return CollectionUtils.containsAny(value.f0.getMentions(), authorTweetMentions);
    }
}
