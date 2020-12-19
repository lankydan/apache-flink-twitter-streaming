package dev.lankydan.flink.twitter.functions;

import dev.lankydan.flink.twitter.data.RecentTweet;
import dev.lankydan.flink.twitter.data.StreamedTweet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.List;
import java.util.stream.Collectors;

public class FilterByRepeatedMentions implements MapFunction<Tuple2<StreamedTweet, List<RecentTweet>>, Tuple2<StreamedTweet, List<RecentTweet>>> {

    @Override
    public Tuple2<StreamedTweet, List<RecentTweet>> map(Tuple2<StreamedTweet, List<RecentTweet>> value) {
        StreamedTweet streamedTweet = value.f0;

        List<RecentTweet> filteredRecentTweets =  value.f1.stream()
            .filter(tweet -> !tweet.getTweet().getId().equals(streamedTweet.getTweet().getId()))
            .filter(tweet -> CollectionUtils.containsAny(streamedTweet.getMentions(), tweet.getMentions()))
            .collect(Collectors.toList());

        return Tuple2.of(streamedTweet, filteredRecentTweets);
    }
}
