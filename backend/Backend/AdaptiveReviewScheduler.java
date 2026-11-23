package Backend;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import model.ReviewRecord;

/**
 * Adaptive spaced-repetition scheduler (SM-2 inspired).
 *
 * The schedule is replayed from a word's review history, so no database
 * schema change is needed. Each word has its own "ease" factor: words you
 * answer correctly drift towards longer intervals, words you miss get
 * shorter ones. Reviewing late (a long recall gap) earns extra credit.
 */
public class AdaptiveReviewScheduler {

    public static final double INITIAL_EASE = 2.5;
    public static final double MIN_EASE = 1.3;
    public static final double MAX_EASE = 3.0;
    public static final double MAX_INTERVAL_DAYS = 180.0;

    /** After a wrong answer the word comes back after this delay. */
    public static final Duration RELEARN_DELAY = Duration.ofMinutes(10);

    /** Result of replaying a review history. */
    public static class Schedule {
        public final LocalDateTime nextReviewAt;
        public final double easeFactor;
        public final double intervalDays;
        public final int lapses;

        Schedule(LocalDateTime nextReviewAt, double easeFactor,
                 double intervalDays, int lapses) {
            this.nextReviewAt = nextReviewAt;
            this.easeFactor = easeFactor;
            this.intervalDays = intervalDays;
            this.lapses = lapses;
        }
    }

    public Schedule calculate(List<ReviewRecord> reviews, LocalDateTime now) {

        if (reviews == null || reviews.isEmpty()) {
            return new Schedule(now, INITIAL_EASE, 0.0, 0);
        }

        double ease = INITIAL_EASE;
        double intervalDays = 0.0;
        int lapses = 0;
        int successes = 0;
        LocalDateTime previousReview = null;
        LocalDateTime scheduledFor = null;
        LocalDateTime next = now;

        for (ReviewRecord r : reviews) {

            LocalDateTime at = r.getReviewedAt();

            if (r.isCorrect()) {

                successes++;

                // Reviewing later than planned and still remembering
                // is stronger evidence of memory than an on-time review.
                double lateBonus = 1.0;
                if (scheduledFor != null && at.isAfter(scheduledFor)) {
                    double lateDays = Duration.between(scheduledFor, at)
                            .toMinutes() / 1440.0;
                    lateBonus += Math.min(lateDays / Math.max(intervalDays, 1.0), 1.0) * 0.25;
                }

                if (successes == 1) {
                    intervalDays = 1.0;
                } else if (successes == 2) {
                    intervalDays = 3.0;
                } else {
                    intervalDays = intervalDays * ease * lateBonus;
                }

                ease = Math.min(MAX_EASE, ease + 0.1);
                intervalDays = Math.min(intervalDays, MAX_INTERVAL_DAYS);
                next = at.plusMinutes(Math.round(intervalDays * 1440));

            } else {

                lapses++;
                successes = 0;
                ease = Math.max(MIN_EASE, ease - 0.2);
                // A forgotten word keeps a fraction of its old interval
                // as the target to rebuild towards.
                intervalDays = Math.max(0.0, intervalDays * 0.25);
                next = at.plus(RELEARN_DELAY);
            }

            previousReview = at;
            scheduledFor = next;
        }

        return new Schedule(next, ease, intervalDays, lapses);
    }

    public LocalDateTime calculateNextReviewAt(List<ReviewRecord> reviews) {
        return calculate(reviews, LocalDateTime.now()).nextReviewAt;
    }
}
