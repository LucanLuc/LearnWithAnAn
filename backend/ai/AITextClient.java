
package ai;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class AITextClient {

    private static final String TEXT_URL =
            "https://gen.pollinations.ai/text/";

    private static final String TEXT_MODEL =
            "openai/gpt-4o-mini";

    private final HttpClient httpClient;

    public AITextClient() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    public String generateText(String prompt)
            throws IOException, InterruptedException {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException(
                    "Prompt cannot be empty."
            );
        }

        String encodedPrompt = URLEncoder.encode(
                prompt,
                StandardCharsets.UTF_8
        ).replace("+", "%20");

        String encodedModel = URLEncoder.encode(
                TEXT_MODEL,
                StandardCharsets.UTF_8
        );

        String url = TEXT_URL
                + encodedPrompt
                + "?model=" + encodedModel;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(90))
                .header(
                        "Authorization",
                        "Bearer " + AIConfig.getApiKey()
                )
                .header("Accept", "text/plain")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(
                        StandardCharsets.UTF_8
                )
        );

        if (response.statusCode() != 200) {
            throw new IOException(
                    "AI text generation failed. HTTP "
                    + response.statusCode()
                    + ": " + response.body()
            );
        }

        String generatedText = response.body().trim();

        if (generatedText.isEmpty()) {
            throw new IOException(
                    "The AI returned an empty response."
            );
        }

        return generatedText;
    }
}
