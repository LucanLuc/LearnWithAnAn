
package model;

public class AIExercise {

    private String question;
    private String[] options;
    private String correctAnswer;
    private String explanation;
    private String difficulty;

    public AIExercise(
            String question,
            String[] options,
            String correctAnswer,
            String explanation,
            String difficulty) {

        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer.toUpperCase();
        this.explanation = explanation;
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public boolean checkAnswer(String answer) {
        return answer != null &&
                correctAnswer.equalsIgnoreCase(answer.trim());
    }
}
