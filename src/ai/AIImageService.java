
package ai;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import model.Word;

public class AIImageService {

    private final AIClient aiClient;

    public AIImageService() {
        this.aiClient = new AIClient();
    }

    // Generate an illustration for a vocabulary word
    public String generateImage(Word word)
            throws IOException, InterruptedException {

        if (word == null) {
            throw new IllegalArgumentException(
                    "Word cannot be null."
            );
        }

        String prompt = createPrompt(word);

        String jsonBody = """
                {
                    "model": "%s",
                    "prompt": "%s",
                    "size": "1024x1024",
                    "quality": "medium",
                    "output_format": "png",
                    "n": 1
                }
                """.formatted(
                    AIConfig.getImageModel(),
                    escapeJson(prompt)
                );

        // Send request to OpenAI
        String response = aiClient.postJson(jsonBody);

        // Extract Base64 image data
        String base64Image = extractImageData(response);

        // Decode Base64 into image bytes
        byte[] imageBytes =
                Base64.getDecoder().decode(base64Image);

        // Create image directory
        Path imageDirectory = AIConfig.getImageDirectory();

        Files.createDirectories(imageDirectory);

        // Generate filename
        String filename = createFilename(word);

        Path imageFile = imageDirectory.resolve(filename);

        // Save generated image
        Files.write(
                imageFile,
                imageBytes,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        // Update Word object
        String imagePath = imageFile.toString();

        word.setImagePath(imagePath);

        System.out.println(
                "Vocabulary image saved: " + imagePath
        );

        return imagePath;
    }


    // Build the image-generation prompt
    private String createPrompt(Word word) {

        return """
                Create an educational vocabulary illustration.

                Vocabulary: %s

                Definition: %s

                Requirements:
                - Visually represent the meaning of the word.
                - Make the concept easy to understand and remember.
                - Use a clean, colorful illustration style.
                - Use a simple background.
                - Do not include written words or captions.
                - Make the image suitable for a vocabulary learning app.
                """.formatted(
                    word.getWord(),
                    word.getDefinition()
                );
    }


    // Escape special characters for JSON
    private String escapeJson(String text) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }


    // Extract Base64 image data from API response
    private String extractImageData(String response)
            throws IOException {

        Pattern pattern = Pattern.compile(
                "\"b64_json\"\\s*:\\s*\"([A-Za-z0-9+/=]+)\""
        );

        Matcher matcher = pattern.matcher(response);

        if (!matcher.find()) {
            throw new IOException(
                    "No generated image data found in API response."
            );
        }

        return matcher.group(1);
    }


    // Create a safe filename
    private String createFilename(Word word) {

        String safeName = word.getWord()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");

        return safeName + ".png";
    }
}
