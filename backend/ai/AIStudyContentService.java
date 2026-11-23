
package ai;

import model.Word;

public class AIStudyContentService {

    private final AITextClient textClient;

    public AIStudyContentService() {
        textClient = new AITextClient();
    }

    public String generateLearningContent(Word word)
            throws Exception {

        if (word == null) {
            throw new IllegalArgumentException(
                    "Word cannot be null."
            );
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
                """.formatted(
                    word.getWord(),
                    word.getDefinition()
                );

        return textClient.generateText(prompt);
    }
}
