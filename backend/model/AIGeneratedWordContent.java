
package model;

public class AIGeneratedWordContent {

    private final String translation;
    private final String simpleExplanation;
    private final String exampleSentence;

    public AIGeneratedWordContent(
            String translation,
            String simpleExplanation,
            String exampleSentence
    ) {
        if (translation == null || translation.isBlank()
                || simpleExplanation == null || simpleExplanation.isBlank()
                || exampleSentence == null || exampleSentence.isBlank()) {
            throw new IllegalArgumentException(
                    "Generated vocabulary content cannot be empty."
            );
        }

        this.translation = translation.trim();
        this.simpleExplanation = simpleExplanation.trim();
        this.exampleSentence = exampleSentence.trim();
    }

    public String getTranslation() {
        return translation;
    }

    public String getSimpleExplanation() {
        return simpleExplanation;
    }

    public String getExampleSentence() {
        return exampleSentence;
    }
}
