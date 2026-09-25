package Backend;

public class ReviewResult {

    private boolean correct;
    private String correctAnswer;

    public ReviewResult(boolean correct, String correctAnswer) {
        this.correct = correct;
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrect() {
        return correct;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
    
}