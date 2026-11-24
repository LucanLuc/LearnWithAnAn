
package model;

import java.time.LocalDateTime;

public class Word {

    private String word;
    private String definition;
    private Character difficulty;

    private LocalDateTime dateAdded;
    private LocalDateTime nextReviewAt;

    private int correctCount;
    private int wrongCount;

    private String translation;
    private String simpleExplanation;
    private String exampleSentence;

    private String imagePath;

    public Word(
            String word,
            String definition,
            LocalDateTime time,
            int correctCount,
            int wrongCount
    ) {
        this.word = word;
        this.definition = definition;
        this.dateAdded = time;
        this.correctCount = correctCount;
        this.wrongCount = wrongCount;
    }

    public void incrementCorrect() {
        correctCount++;
    }

    public void incrementWrong() {
        wrongCount++;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Character getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Character difficulty) {
        this.difficulty = difficulty;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public LocalDateTime getNextReviewAt() {
        return nextReviewAt;
    }

    public void setNextReviewAt(LocalDateTime nextReviewAt) {
        this.nextReviewAt = nextReviewAt;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getIncorrectCount() {
        return wrongCount;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getSimpleExplanation() {
        return simpleExplanation;
    }

    public void setSimpleExplanation(String simpleExplanation) {
        this.simpleExplanation = simpleExplanation;
    }

    public String getExampleSentence() {
        return exampleSentence;
    }

    public void setExampleSentence(String exampleSentence) {
        this.exampleSentence = exampleSentence;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
