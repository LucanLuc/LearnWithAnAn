
package ai;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class AIClient {

    private final HttpClient httpClient;

    public AIClient() {

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    // Send a POST request to the AI provider
    public String postJson(String jsonBody)
            throws IOException, InterruptedException {

        String apiKey = AIConfig.getApiKey();

        HttpRequest request = HttpRequest.newBuilder()

                .uri(URI.create(AIConfig.getApiUrl()))

                .timeout(Duration.ofMinutes(3))

                .header("Authorization", "Bearer " + apiKey)

                .header("Content-Type", "application/json")

                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))

                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        // Check whether the request succeeded
        if (response.statusCode() < 200 ||
                response.statusCode() >= 300) {

            throw new IOException(
                    "AI API request failed.\n" +
                    "HTTP Status: " + response.statusCode() +
                    "\nResponse: " + response.body()
            );
        }

        return response.body();
    }
}
