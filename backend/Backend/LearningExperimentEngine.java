
package Backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.ExperimentStrategy;
import model.ExperimentMetrics;
import model.LearningExperimentAttempt;

public class LearningExperimentEngine {

    private final List<LearningExperimentAttempt> attempts =
            new ArrayList<>();

    public void recordAttempt(LearningExperimentAttempt attempt) {

        if (attempt == null) {
            throw new IllegalArgumentException(
                    "Attempt cannot be null."
            );
        }

        attempts.add(attempt);
    }

    public List<LearningExperimentAttempt> getAllAttempts() {

        return Collections.unmodifiableList(attempts);
    }

    public ExperimentMetrics calculateMetrics(
            ExperimentStrategy strategy) {

        int total = 0;
        int correct = 0;

        int delayedTotal = 0;
        int delayedCorrect = 0;

        long totalResponseTime = 0;

        for (LearningExperimentAttempt attempt : attempts) {

            if (attempt.getStrategy() != strategy) {
                continue;
            }

            if (attempt.isDelayedRetest()) {

                delayedTotal++;

                if (attempt.isCorrect()) {
                    delayedCorrect++;
                }

            } else {

                total++;

                if (attempt.isCorrect()) {
                    correct++;
                }

                totalResponseTime +=
                        attempt.getResponseTimeMillis();
            }
        }

        return new ExperimentMetrics(
                strategy,
                total,
                correct,
                delayedTotal,
                delayedCorrect,
                totalResponseTime
        );
    }

    public List<ExperimentMetrics> getAllStrategyMetrics() {

        List<ExperimentMetrics> metrics = new ArrayList<>();

        for (ExperimentStrategy strategy :
                ExperimentStrategy.values()) {

            metrics.add(calculateMetrics(strategy));
        }

        return metrics;
    }

    public void clearExperiment() {
        attempts.clear();
    }
}
