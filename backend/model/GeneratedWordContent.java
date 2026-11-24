
package model;

public class GeneratedWordContent {

    private String translation;
    private String simpleExplanation;
    private String exampleSentence;

    public GeneratedWordContent(
            String translation,
            String simpleExplanation,
            String exampleSentence) {

        this.translation = translation;
        this.simpleExplanation = simpleExplanation;
        this.exampleSentence = exampleSentence;
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
