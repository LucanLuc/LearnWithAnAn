
package Backend;

import model.LearningState;

public class ReviewPriorityService {

    /**
     * Calculates how urgently a word should be reviewed.
     * Higher score = higher selection probability.
     */
    public double calculatePriority(WordLearningStats stats) {

        // A word with no learning history is a new word.
        if (stats == null || stats.getTotalReviews() == 0) {
            return 1000.0;
        }

        double priority = 0.0;

        LearningState state = stats.getLearningState();

        // 1. Learning difficulty
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

        priority += (100.0 - accuracy) * 0.5;

        // 3. Recent performance
        // Recent mistakes have a strong influence.
        double recentAccuracy = stats.getRecentAccuracy(3);

        priority += (100.0 - recentAccuracy) * 1.0;

        // 4. Historical mistakes
        // Limit this contribution so old mistakes
        // do not dominate the algorithm forever.
        double mistakeBonus = Math.min(
                stats.getIncorrectReviews() * 5.0,
                30.0
        );

        priority += mistakeBonus;

        // 5. Correct streak
        // Repeated successful answers reduce urgency.
        int correctStreak = stats.getCorrectStreak();

        double streakReduction = Math.min(
                correctStreak * 5.0,
                25.0
        );

        priority -= streakReduction;

        // 6. Time since last review
        long hoursSinceReview =
                stats.getHoursSinceLastReview();

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

        // Every reviewed word must have a positive score.
        return Math.max(priority, 1.0);
    }


	public double calculatePriority(
	        WordLearningStats stats,
	        double aiAccuracy,
	        int aiExerciseCount) {
	
	    // Start with the normal spaced-repetition priority.
	    double priority = calculatePriority(stats);
	
	    // No AI exercise history means no AI adjustment.
	    if (aiExerciseCount <= 0) {
	        return priority;
	    }
	
	    // Protect against invalid accuracy values.
	    if (Double.isNaN(aiAccuracy)
	            || Double.isInfinite(aiAccuracy)) {
	        return priority;
	    }
	
	    aiAccuracy = Math.max(0.0, Math.min(100.0, aiAccuracy));
	
	    // Lower AI accuracy means greater learning difficulty.
	    double aiDifficultyBonus =
	            (100.0 - aiAccuracy) * 0.5;
	
	    // Maximum AI contribution: 50 points.
	    aiDifficultyBonus = Math.min(aiDifficultyBonus, 50.0);
	
	    // Confidence increases with the number of attempts.
	    double confidence = Math.min(
	            aiExerciseCount / 5.0,
	            1.0
	    );
	
	    priority += aiDifficultyBonus * confidence;
	
	    return Math.max(priority, 1.0);
	}
}
