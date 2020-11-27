package dev.lankydan.flink.kafka.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Base64;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class TwitterClient {

    private final OkHttpClient client = new OkHttpClient();

    private final ObjectMapper mapper = new ObjectMapper();

    private String bearerToken;

    public TwitterClient() {
    }

    void authorize(String apiKey, String apiSecretKey) throws IOException {
        Request request = new Request.Builder()
            .post(RequestBody.create(new byte[]{}))
            .url("https://api.twitter.com/oauth2/token?grant_type=client_credentials")
            .addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((apiKey + ":" + apiSecretKey).getBytes()))
            .build();
        JsonNode json = mapper.readValue(client.newCall(request).execute().body().bytes(), JsonNode.class);
        bearerToken = json.get("access_token").asText();
    }

    public CompletableFuture<String> enrich(String id) {
        Request request = new Request.Builder().get()
            .url(
                "https://api.twitter.com/2/tweets?ids=" + id +
                    "&expansions=geo.place_id,author_id" +
                    "&tweet.fields=public_metrics,entities,created_at,author_id" +
                    "&place.fields=contained_within,country,country_code,full_name,geo,id,name,place_type" +
                    "&user.fields=name,username,public_metrics")
            .addHeader("Authorization", "Bearer " + bearerToken)
            .build();
        CompletableFuture<String> future = new CompletableFuture<>();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                future.complete(response.body().string());
            }
        });
        return future;
    }

    public static TwitterClient create() throws IOException {
        Properties properties = new Properties();
        properties.load(TwitterClient.class.getClassLoader().getResourceAsStream("application.properties"));
        TwitterClient client = new TwitterClient();
        client.authorize(properties.getProperty("api.key"), properties.getProperty("api.secret.key"));
        return client;
    }
}
