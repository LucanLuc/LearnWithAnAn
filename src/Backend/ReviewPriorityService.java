package Backend;

public class ReviewPriorityService {

	public double calculatePriority(
	        WordLearningStats stats) {

	    /*
	     * NEW words should be introduced quickly.
	     */
	    if (stats.getTotalReviews() == 0) {
	        return 1000.0;
	    }

	    double priority = 0.0;

	    /*
	     * --------------------------------
	     * 1. Learning state
	     * --------------------------------
	     *
	     * We start with a base priority.
	     */
	    LearningState state =
	        stats.getLearningState();

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
	     * --------------------------------
	     * 2. Overall accuracy
	     * --------------------------------
	     *
	     * Lower accuracy = higher priority.
	     */
	    double accuracy =
	        stats.getAccuracy();

	    priority +=
	        (100 - accuracy);


	    /*
	     * --------------------------------
	     * 3. Recent accuracy
	     * --------------------------------
	     *
	     * Recent performance is important
	     * because the learner may be improving
	     * or getting worse.
	     */
	    double recentAccuracy =
	        stats.getRecentAccuracy(3);

	    priority +=
	        (100 - recentAccuracy) * 0.5;


	    /*
	     * --------------------------------
	     * 4. Incorrect answers
	     * --------------------------------
	     *
	     * Words that have repeatedly caused
	     * problems receive extra priority.
	     */
	    priority +=
	        stats.getIncorrectReviews() * 15;


	    /*
	     * --------------------------------
	     * 5. Correct streak
	     * --------------------------------
	     *
	     * A long streak slightly reduces
	     * the priority.
	     */
	    int correctStreak =
	        stats.getCorrectStreak();

	    priority -=
	        correctStreak * 5;


	    /*
	     * --------------------------------
	     * 6. Time since last review
	     * --------------------------------
	     */
	    long hoursSinceReview =
	        stats.getHoursSinceLastReview();

	    if (hoursSinceReview != Long.MAX_VALUE) {

	        double recencyBonus =
	            Math.min(
	                hoursSinceReview * 2,
	                100
	            );

	        /*
	         * Mastered words can wait longer.
	         */
	        if (state == LearningState.MASTERED) {

	            recencyBonus *= 0.5;

	        } else if (state == LearningState.FAMILIAR) {

	            recencyBonus *= 0.75;
	        }

	        priority += recencyBonus;
	    }


	    /*
	     * --------------------------------
	     * 7. Prevent negative priority
	     * --------------------------------
	     */
	    if (priority < 0) {
	        priority = 0;
	    }

	    return priority;
	}
}