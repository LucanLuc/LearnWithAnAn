package Backend;

import java.time.LocalDateTime;

public class ReviewResult {

    private Word word;
    private boolean correct;
    private LocalDateTime reviewedAt;

    public ReviewResult(Word word, boolean correct) {
        this.word = word;
        this.correct = correct;
        this.reviewedAt = LocalDateTime.now();
    }

    public Word getWord() {
        return word;
    }

    public boolean isCorrect() {
        return correct;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
}