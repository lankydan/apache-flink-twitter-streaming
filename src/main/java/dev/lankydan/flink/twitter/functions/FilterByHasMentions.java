package dev.lankydan.flink.twitter.functions;

import dev.lankydan.flink.twitter.data.RecentTweet;
import dev.lankydan.flink.twitter.data.StreamedTweet;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.List;

public class FilterByHasMentions implements FilterFunction<Tuple2<StreamedTweet, List<RecentTweet>>> {

    @Override
    public boolean filter(Tuple2<StreamedTweet, List<RecentTweet>> value) {
        return value.f1.stream().anyMatch(tweet -> !tweet.getMentions().isEmpty());
    }
}

