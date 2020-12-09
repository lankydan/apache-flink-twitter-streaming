package dev.lankydan.flink.twitter.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Entities {

    private List<Mention> mentions;

    public Entities() {
    }

    public List<Mention> getMentions() {
        return mentions;
    }

    public void setMentions(List<Mention> mentions) {
        this.mentions = mentions;
    }

    @Override
    public String toString() {
        return "Entities{" +
            "mentions=" + mentions +
            '}';
    }
}
