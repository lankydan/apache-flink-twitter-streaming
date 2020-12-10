package dev.lankydan.flink.twitter.source;

import org.apache.flink.streaming.connectors.twitter.TwitterSource;

import java.io.IOException;
import java.util.Properties;

public class TwitterSourceCreator {

    public static TwitterSource create() {
        try {
            Properties properties = new Properties();
            properties.load(TwitterSource.class.getClassLoader().getResourceAsStream("application.properties"));
            properties.setProperty(TwitterSource.CONSUMER_KEY, properties.getProperty("api.key"));
            properties.setProperty(TwitterSource.CONSUMER_SECRET, properties.getProperty("api.secret.key"));
            properties.setProperty(TwitterSource.TOKEN, properties.getProperty("api.token"));
            properties.setProperty(TwitterSource.TOKEN_SECRET, properties.getProperty("api.secret.token"));
            return new TwitterSource(properties);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load properties");
        }
    }
}
