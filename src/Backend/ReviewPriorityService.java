package Backend;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

public class ReviewPriorityService {

    public double calculatePriority(
            WordLearningStats stats) {

        // Never reviewed before
        if (stats.getTotalReviews() == 0) {
            return 100.0;
        }

        double priority = 0.0;

        // -------------------------
        // 1. Incorrect answers
        // -------------------------

        priority += stats.getIncorrectReviews() * 10;

        // -------------------------
        // 2. Low accuracy
        // -------------------------

        double accuracy = stats.getAccuracy();

        priority += (100 - accuracy);

        // -------------------------
        // 3. Time since last review
        // -------------------------

        if (stats.getLastReviewedAt() != null) {

            long hoursSinceReview =
                Duration.between(
                    stats.getLastReviewedAt(),
                    LocalDateTime.now()
                ).toHours();

            priority += Math.min(
                hoursSinceReview,
                50
            );
        }

        return priority;
    }
}