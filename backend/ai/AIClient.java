package ai;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class AIClient {

    private final HttpClient httpClient;

    public AIClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    public byte[] generateImage(String prompt)
            throws IOException, InterruptedException {

        String encodedPrompt = URLEncoder
                .encode(prompt, StandardCharsets.UTF_8)
                .replace("+", "%20");

        String encodedModel = URLEncoder
                .encode(AIConfig.getImageModel(), StandardCharsets.UTF_8);

        String url = AIConfig.getApiUrl()
                + encodedPrompt
                + "?model=" + encodedModel;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(180))
                .header("Authorization",
                        "Bearer " + AIConfig.getApiKey())
                .header("Accept", "image/*")
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofByteArray()
        );

        if (response.statusCode() != 200) {
            String errorBody = new String(
                    response.body(),
                    StandardCharsets.UTF_8
            );

            throw new IOException(
                    "Pollinations API error: HTTP "
                    + response.statusCode()
                    + "\n" + errorBody
            );
        }

        String contentType = response.headers()
                .firstValue("Content-Type")
                .orElse("");

        if (!contentType.startsWith("image/")) {
            throw new IOException(
                    "Expected an image, but received: " + contentType
            );
        }

        return response.body();
    }
}