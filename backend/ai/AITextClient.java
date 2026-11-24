
package ai;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AITextClient {

    private static final String OLLAMA_URL =
            "http://localhost:11434/api/chat";

    private static final String MODEL = "qwen3:4b-instruct";
    
    private final HttpClient client;
    private final ObjectMapper mapper;

    public AITextClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        this.mapper = new ObjectMapper();
    }

    public String generateText(String prompt) throws Exception {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException(
                    "Prompt cannot be empty.");
        }

        // Create the JSON request body.
        ObjectNode requestBody = mapper.createObjectNode();

        requestBody.put("model", MODEL);

        ObjectNode message = mapper.createObjectNode();
        message.put("role", "user");
        message.put("content", prompt);

        requestBody.putArray("messages").add(message);

        // Disable streaming so we receive one complete response.
        requestBody.put("stream", false);

        // Disable thinking output to keep responses concise.
     // Disable thinking output to keep responses concise.
     // Disable thinking output.
        requestBody.put("think", false);

        String jsonBody = mapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_URL))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofMinutes(10))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException(
                    "Ollama request failed. HTTP status: "
                    + response.statusCode()
                    + "\nResponse: " + response.body());
        }

        // Parse Ollama's JSON response.
        JsonNode root = mapper.readTree(response.body());
        
        System.out.println("OLLAMA RAW RESPONSE:");
        System.out.println(response.body());

        JsonNode contentNode = root.path("message").path("content");

        if (contentNode.isMissingNode()
                || contentNode.asText().isBlank()) {
            throw new IOException(
                    "Ollama returned an empty response.");
        }

        return contentNode.asText().trim();
    }
}
