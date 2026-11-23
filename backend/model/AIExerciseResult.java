
package model;

import java.time.LocalDateTime;

public class AIExerciseResult {

    private String word;
    private String difficulty;
    private boolean correct;
    private String selectedAnswer;
    private String correctAnswer;
    private LocalDateTime completedAt;

    public AIExerciseResult(
            String word,
            String difficulty,
            boolean correct,
            String selectedAnswer,
            String correctAnswer) {

        this.word = word;
        this.difficulty = difficulty;
        this.correct = correct;
        this.selectedAnswer = selectedAnswer;
        this.correctAnswer = correctAnswer;
        this.completedAt = completedAt; 
    }

    public String getWord() {
        return word;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public boolean isCorrect() {
        return correct;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}
