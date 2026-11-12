
package Backend;

import java.time.LocalDateTime;

public class ReviewScheduleService {

    /**
     * Calculates when a word should next be reviewed.
     */
    public LocalDateTime calculateNextReviewAt(
            WordLearningStats stats) {

        // A new word should be reviewed immediately.
        if (stats == null || stats.getTotalReviews() == 0) {
            return LocalDateTime.now();
        }

        // An incorrect answer resets the successful streak.
        if (stats.getCorrectStreak() == 0) {
            return LocalDateTime.now();
        }

        int streak = stats.getCorrectStreak();

        int intervalDays;

        if (streak == 1) {
            intervalDays = 1;
        } else if (streak == 2) {
            intervalDays = 3;
        } else if (streak == 3) {
            intervalDays = 7;
        } else {
            intervalDays = 14;
        }

        return stats.getLastReviewedAt()
                .plusDays(intervalDays);
    }

    /**
     * Checks whether a word is due for review.
     */
    public boolean isDue(WordLearningStats stats) {

        return !calculateNextReviewAt(stats)
                .isAfter(LocalDateTime.now());
    }

    /**
     * Returns the number of hours until the next review.
     * Negative values mean the word is overdue.
     */
    public long getHoursUntilDue(WordLearningStats stats) {

        return java.time.Duration.between(
                LocalDateTime.now(),
                calculateNextReviewAt(stats)
        ).toHours();
    }
}
