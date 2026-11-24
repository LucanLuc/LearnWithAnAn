
package ai;

import model.Word;
import model.AIGeneratedWordContent;

public class AIStudyContentService {

    private final AITextClient textClient;

    public AIStudyContentService() {
        textClient = new AITextClient();
    }

    // Existing AI learning guide
    public String generateLearningContent(Word word) throws Exception {

        if (word == null) {
            throw new IllegalArgumentException("Word cannot be null.");
        }

        String prompt = """
                You are an expert English vocabulary tutor.

                Create a short, useful learning guide for this word.

                WORD: %s

                DEFINITION: %s

                Follow this exact structure:

                MEANING

                Explain the meaning in simple English.

                EXAMPLE SENTENCES

                Give two natural example sentences.

                Make the contexts different.

                COMMON USAGE

                Explain when and how people normally use this word.

                MEMORY TIP

                Give one memorable way to remember the word.

                COMMON MISTAKE

                Mention one common mistake learners make with this word.

                Requirements:

                - Use clear language suitable for an English learner.
                - Keep explanations accurate and practical.
                - Do not invent alternative meanings.
                - Do not include unrelated information.
                - Do not use Markdown tables.
                """.formatted(word.getWord(), word.getDefinition());

        return textClient.generateText(prompt);
    }

    // Generate details when adding a new vocabulary word
    public AIGeneratedWordContent generateVocabularyContent(
            String word
    ) throws Exception {

        if (word == null || word.isBlank()) {
            throw new IllegalArgumentException(
                    "Word cannot be empty."
            );
        }

        String prompt = """
                You are an expert English vocabulary tutor
                helping Vietnamese-speaking English learners.

                Generate vocabulary information for this English word:

                WORD: %s

                Return exactly three lines using this format:

                TRANSLATION: Vietnamese translation
                SIMPLE_EXPLANATION: A short and easy English explanation
                EXAMPLE_SENTENCE: One natural English example sentence

                Requirements:
                - Translate the word into Vietnamese accurately.
                - Explain the word using simple English.
                - Make the explanation easy for beginners to understand.
                - Give exactly one natural example sentence.
                - The example must demonstrate the meaning of the word.
                - Do not include pronunciation.
                - Do not include additional meanings unless necessary.
                - Do not add introductions or conclusions.
                - Do not use Markdown or bullet points.
                - Keep each result on one line.
                """.formatted(word.trim());

        String response = textClient.generateText(prompt);

        String translation = null;
        String explanation = null;
        String example = null;

        String[] lines = response.split("\\R");

        for (String line : lines) {

            String cleanedLine = line.trim();

            if (cleanedLine.startsWith("TRANSLATION:")) {
                translation = cleanedLine.substring(
                        "TRANSLATION:".length()
                ).trim();
            }

            else if (cleanedLine.startsWith("SIMPLE_EXPLANATION:")) {
                explanation = cleanedLine.substring(
                        "SIMPLE_EXPLANATION:".length()
                ).trim();
            }

            else if (cleanedLine.startsWith("EXAMPLE_SENTENCE:")) {
                example = cleanedLine.substring(
                        "EXAMPLE_SENTENCE:".length()
                ).trim();
            }
        }

        if (translation == null || translation.isBlank()
                || explanation == null || explanation.isBlank()
                || example == null || example.isBlank()) {

            throw new Exception(
                    "Could not understand the AI response. Please try again.\n\n"
                    + response
            );
        }

        return new AIGeneratedWordContent(
                translation,
                explanation,
                example
        );
    }
}
