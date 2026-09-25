package Backend;

public class ReviewSummary {

    private int correct;
    private int incorrect;
    private int total;

    public ReviewSummary(int correct, int incorrect) {
        this.correct = correct;
        this.incorrect = incorrect;
        this.total = correct + incorrect;
    }

    public int getCorrect() {
        return correct;
    }

    public int getIncorrect() {
        return incorrect;
    }

    public int getTotal() {
        return total;
    }
}