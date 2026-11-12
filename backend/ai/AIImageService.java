package ai;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import model.Word;

public class AIImageService {

    private final AIClient aiClient;

    public AIImageService() {
        this.aiClient = new AIClient();
    }

    public String generateImage(Word word)
            throws IOException, InterruptedException {

        if (word == null) {
            throw new IllegalArgumentException(
                    "Word cannot be null."
            );
        }

        // 1. Create an educational image prompt
        String prompt = createPrompt(word);

        // 2. Ask Pollinations to generate the image
        byte[] imageBytes = aiClient.generateImage(prompt);

        // 3. Create the images directory if necessary
        Path imageDirectory = AIConfig.getImageDirectory();
        Files.createDirectories(imageDirectory);

        // 4. Create a filename for this vocabulary word
        String filename = createFilename(word);
        Path imageFile = imageDirectory.resolve(filename);

        // 5. Save the image
        Files.write(
                imageFile,
                imageBytes,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        // 6. Store the image path in the Word object
        String imagePath = imageFile.toString();
        word.setImagePath(imagePath);

        System.out.println(
                "Vocabulary image saved: " + imagePath
        );

        return imagePath;
    }

    private String createPrompt(Word word) {

        return """
                Create an educational vocabulary illustration.

                Vocabulary: %s
                Definition: %s

                Requirements:
                - Visually represent the meaning of the vocabulary.
                - Make the concept easy to understand and remember.
                - Use a clean, colorful illustration style.
                - Use a simple background.
                - Do not include written words or captions.
                - Make the image suitable for a vocabulary learning app.
                - Make the visual concept accurate to the definition.
                """.formatted(
                    word.getWord(),
                    word.getDefinition()
                );
    }

    private String createFilename(Word word) {

        String safeWord = word.getWord()
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "_");

        return safeWord + ".jpg";
    }
}