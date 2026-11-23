package ai;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import model.Word;

/**
 * Section 3 - AI-assisted learning.
 * Generates example sentences, a short explanation and a fill-in-the-blank
 * exercise for a word. If the text API is unavailable (no key, offline) it
 * falls back to a simple template so the app keeps working.
 *
 * The text endpoint is configurable via AI_TEXT_URL because it has not been
 * verified here; the default assumes a "GET {base}{prompt}" style API.
 */
public class AIStudyService {

    public static class StudyMaterial {
        public final String explanation;
        public final List<String> examples;
        public final String exercise;   // sentence with ____ in place of the word
        public final boolean fromAI;

        public StudyMaterial(String explanation, List<String> examples,
                             String exercise, boolean fromAI) {
            this.explanation = explanation;
            this.examples = examples;
            this.exercise = exercise;
            this.fromAI = fromAI;
        }
    }

    private static final String DEFAULT_TEXT_URL = "https://gen.pollinations.ai/text/";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15)).build();

    public StudyMaterial generate(Word word) {
        try {
            String raw = callTextApi(buildPrompt(word));
            StudyMaterial parsed = parse(raw, word.getWord());
            if (parsed != null) return parsed;
        } catch (Exception e) {
            System.err.println("AI study material failed, using fallback: " + e.getMessage());
        }
        return fallback(word);
    }

    String buildPrompt(Word word) {
        return """
            You are a vocabulary tutor. For the word "%s" (definition: %s) reply in EXACTLY this format, nothing else:
            EXPLANATION: <one or two simple sentences>
            EXAMPLE1: <natural sentence using the word>
            EXAMPLE2: <another natural sentence using the word>
            EXERCISE: <a sentence where the word is replaced by ____>
            """.formatted(word.getWord(), word.getDefinition());
    }

    /** Parses the line-based reply. Returns null if it is malformed. */
    StudyMaterial parse(String raw, String wordText) {
        if (raw == null) return null;

        String explanation = null, exercise = null;
        List<String> examples = new ArrayList<>();

        for (String line : raw.split("\\R")) {
            String t = line.trim();
            if (t.startsWith("EXPLANATION:")) explanation = t.substring(12).trim();
            else if (t.startsWith("EXAMPLE1:")) examples.add(t.substring(9).trim());
            else if (t.startsWith("EXAMPLE2:")) examples.add(t.substring(9).trim());
            else if (t.startsWith("EXERCISE:")) exercise = t.substring(9).trim();
        }

        if (explanation == null || examples.isEmpty() || exercise == null
                || !exercise.contains("____")
                || exercise.toLowerCase().contains(wordText.toLowerCase())) {
            return null; // reject exercises that leak the answer
        }
        return new StudyMaterial(explanation, examples, exercise, true);
    }

    StudyMaterial fallback(Word word) {
        List<String> ex = new ArrayList<>();
        ex.add("Meaning of \"" + word.getWord() + "\": " + word.getDefinition() + ".");
        return new StudyMaterial(
            word.getWord() + " means " + word.getDefinition() + ".",
            ex,
            "Which word means \"" + word.getDefinition() + "\"? ____",
            false);
    }

    private String callTextApi(String prompt) throws IOException, InterruptedException {
        String base = System.getenv().getOrDefault("AI_TEXT_URL", DEFAULT_TEXT_URL);
        String url = base + URLEncoder.encode(prompt, StandardCharsets.UTF_8).replace("+", "%20");

        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", "Bearer " + AIConfig.getApiKey())
                .GET().build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new IOException("Text API HTTP " + resp.statusCode());
        }
        return resp.body();
    }
}
