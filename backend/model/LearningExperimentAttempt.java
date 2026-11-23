
package model;

import java.time.LocalDateTime;

public class LearningExperimentAttempt {

    private final ExperimentStrategy strategy;
    private final String word;
    private final boolean correct;
    private final boolean delayedRetest;
    private final long responseTimeMillis;
    private final LocalDateTime attemptedAt;

    public LearningExperimentAttempt(
            ExperimentStrategy strategy,
            String word,
            boolean correct,
            boolean delayedRetest,
            long responseTimeMillis) {

        if (strategy == null || word == null || word.isBlank()) {
            throw new IllegalArgumentException(
                    "Strategy and word are required."
            );
        }

        if (responseTimeMillis < 0) {
            throw new IllegalArgumentException(
                    "Response time cannot be negative."
            );
        }

        this.strategy = strategy;
        this.word = word;
        this.correct = correct;
        this.delayedRetest = delayedRetest;
        this.responseTimeMillis = responseTimeMillis;
        this.attemptedAt = LocalDateTime.now();
    }

    public ExperimentStrategy getStrategy() {
        return strategy;
    }

    public String getWord() {
        return word;
    }

    public boolean isCorrect() {
        return correct;
    }

    public boolean isDelayedRetest() {
        return delayedRetest;
    }

    public long getResponseTimeMillis() {
        return responseTimeMillis;
    }

    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }
}
