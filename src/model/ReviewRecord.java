package Backend;

import java.time.LocalDateTime;

public class ReviewRecord {

    private Word word;
    private boolean correct;
    private LocalDateTime reviewedAt;

    // Used when creating a NEW review
    public ReviewRecord(Word word, boolean correct) {

        this.word = word;
        this.correct = correct;
        this.reviewedAt = LocalDateTime.now();
    }

    // Used when loading an EXISTING review from database
    public ReviewRecord(
            Word word,
            boolean correct,
            LocalDateTime reviewedAt) {

        this.word = word;
        this.correct = correct;
        this.reviewedAt = reviewedAt;
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