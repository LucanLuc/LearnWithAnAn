
package Backend;

import model.LearningState;

public class ReviewPriorityService {

    public double calculatePriority(WordLearningStats stats) {

        // New words should be introduced quickly.
        if (stats.getTotalReviews() == 0) {
            return 1000.0;
        }

        double priority = 0.0;

        // 1. Learning state
        LearningState state = stats.getLearningState();

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

        // 2. Overall accuracy
        double accuracy = stats.getAccuracy();
        priority += (100 - accuracy) * 0.5;

        // 3. Recent accuracy
        double recentAccuracy = stats.getRecentAccuracy(3);
        priority += (100 - recentAccuracy);

        // 4. Historical mistakes (limited bonus)
        double mistakeBonus = Math.min(
                stats.getIncorrectReviews() * 5.0,
                30.0
        );

        priority += mistakeBonus;

        // 5. Correct streak (limited reduction)
        int correctStreak = stats.getCorrectStreak();

        double streakReduction = Math.min(
                correctStreak * 5.0,
                25.0
        );

        priority -= streakReduction;

        // 6. Time since last review
        long hoursSinceReview = stats.getHoursSinceLastReview();

        if (hoursSinceReview != Long.MAX_VALUE) {

            double timeBonus = Math.min(
                    hoursSinceReview * 2.0,
                    100.0
            );

            if (state == LearningState.MASTERED) {
                timeBonus *= 0.5;
            } else if (state == LearningState.FAMILIAR) {
                timeBonus *= 0.75;
            }

            priority += timeBonus;
        }

        // 7. Ensure every reviewed word has a positive weight.
        return Math.max(priority, 1.0);
    }
}
