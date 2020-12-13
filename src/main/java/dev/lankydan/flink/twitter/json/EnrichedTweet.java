package dev.lankydan.flink.twitter.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnrichedTweet {

    private List<EnrichedTweetData> data;
    private Includes includes;

    public EnrichedTweet() {
    }

    public List<EnrichedTweetData> getData() {
        return data;
    }

    public void setData(List<EnrichedTweetData> data) {
        this.data = data;
    }

    public Includes getIncludes() {
        return includes;
    }

    public void setIncludes(Includes includes) {
        this.includes = includes;
    }

    @Override
    public String toString() {
        return "EnrichedTweet{" +
            "data=" + data +
            ", includes=" + includes +
            '}';
    }

    @JsonIgnore
    public EnrichedTweetData getSingleData() {
        return data != null ? data.get(0) : null;
    }
}
