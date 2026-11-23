
package model;

public class ExperimentMetrics {

    private final ExperimentStrategy strategy;

    private final int totalAttempts;
    private final int correctAttempts;

    private final int delayedAttempts;
    private final int delayedCorrect;

    private final long totalResponseTime;

    public ExperimentMetrics(
            ExperimentStrategy strategy,
            int totalAttempts,
            int correctAttempts,
            int delayedAttempts,
            int delayedCorrect,
            long totalResponseTime) {

        this.strategy = strategy;
        this.totalAttempts = totalAttempts;
        this.correctAttempts = correctAttempts;
        this.delayedAttempts = delayedAttempts;
        this.delayedCorrect = delayedCorrect;
        this.totalResponseTime = totalResponseTime;
    }

    public ExperimentStrategy getStrategy() {
        return strategy;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public int getCorrectAttempts() {
        return correctAttempts;
    }

    public double getAccuracy() {

        if (totalAttempts == 0) {
            return 0;
        }

        return correctAttempts * 100.0 / totalAttempts;
    }

    public int getDelayedAttempts() {
        return delayedAttempts;
    }

    public double getDelayedRetention() {

        if (delayedAttempts == 0) {
            return 0;
        }

        return delayedCorrect * 100.0 / delayedAttempts;
    }

    public double getAverageResponseTime() {

        if (totalAttempts == 0) {
            return 0;
        }

        return totalResponseTime / (double) totalAttempts;
    }
}
