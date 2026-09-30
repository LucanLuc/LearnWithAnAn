package Backend;

public class ReviewPriorityService {

    public double calculatePriority(
            WordLearningStats stats) {

        // Never reviewed
        if (stats.getTotalReviews() == 0) {
            return 1000.0;
        }

        double priority = 0.0;

        LearningState state =
            stats.getLearningState();

        long hoursSinceReview =
            stats.getHoursSinceLastReview();

        /*
         * Different learning states have
         * different base priorities.
         */
        switch (state) {

            case WEAK:
                priority += 100;
                break;

            case LEARNING:
                priority += 70;
                break;

            case FAMILIAR:
                priority += 40;
                break;

            case MASTERED:
                priority += 10;
                break;

            case NEW:
                priority += 1000;
                break;
        }

        /*
         * Incorrect answers increase priority.
         */
        priority +=
            stats.getIncorrectReviews() * 15;

        /*
         * Lower accuracy increases priority.
         */
        double overallAccuracy =
        	    stats.getAccuracy();

        double recentAccuracy =
        	    stats.getRecentAccuracy(3);

        priority +=
        	    (100 - overallAccuracy);

        priority +=
        	    (100 - recentAccuracy) * 0.5;

        /*
         * More time since the last review
         * increases priority.
         */
        if (hoursSinceReview != Long.MAX_VALUE) {

            priority += Math.min(
                hoursSinceReview * 2,
                100
            );
        }

        return priority;
    }
}