
package Backend;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import model.Word;

public class IntelligentWordSelector {

    private final ReviewPriorityService priorityService;
    private final ReviewScheduleService scheduleService;
    private final Random random;

    public IntelligentWordSelector() {

        priorityService = new ReviewPriorityService();
        scheduleService = new ReviewScheduleService();
        random = new Random();
    }

    /**
     * Selects the next vocabulary word using
     * adaptive weighted random selection.
     */
    public Word chooseWord(
            List<Word> words,
            Map<Word, WordLearningStats> stats,
            Word previousWord) {

        if (words == null || words.isEmpty()) {
            return null;
        }

        if (stats == null) {
            throw new IllegalArgumentException(
                    "Learning statistics cannot be null."
            );
        }

        LocalDateTime now = LocalDateTime.now();

        // STEP 1: Find all words currently due.
        List<Word> dueWords = new ArrayList<>();

        for (Word word : words) {

            LocalDateTime nextReviewAt =
                    getNextReviewAt(word, stats);

            if (!nextReviewAt.isAfter(now)) {
                dueWords.add(word);
            }
        }

        List<Word> candidates;

        // STEP 2: Prefer words that are due.
        if (!dueWords.isEmpty()) {

            candidates = dueWords;

        } else {

            // If nothing is due, find the earliest
            // upcoming review date.
            LocalDateTime earliestReview = null;

            for (Word word : words) {

                LocalDateTime nextReviewAt =
                        getNextReviewAt(word, stats);

                if (earliestReview == null ||
                        nextReviewAt.isBefore(earliestReview)) {

                    earliestReview = nextReviewAt;
                }
            }

            candidates = new ArrayList<>();

            for (Word word : words) {

                LocalDateTime nextReviewAt =
                        getNextReviewAt(word, stats);

                if (nextReviewAt.equals(earliestReview)) {
                    candidates.add(word);
                }
            }
        }

        // STEP 3: Avoid consecutive repetition
        // whenever another candidate is available.
        if (candidates.size() > 1) {
            candidates.remove(previousWord);
        }

        if (candidates.isEmpty()) {
            return null;
        }

        // STEP 4: Calculate selection weights.
        List<Double> weights = new ArrayList<>();

        double totalWeight = 0.0;

        for (Word word : candidates) {

            WordLearningStats wordStats = stats.get(word);

            // Important: words without statistics
            // are treated as new words.
            double priority =
                    priorityService.calculatePriority(wordStats);

            LocalDateTime nextReviewAt =
                    getNextReviewAt(word, stats);

            // STEP 5: Increase priority for overdue words.
            double overdueBoost = 1.0;

            if (nextReviewAt.isBefore(now)) {

                long overdueDays = Duration.between(
                        nextReviewAt,
                        now
                ).toDays();

                overdueBoost +=
                        Math.min(overdueDays, 30) * 0.1;
            }

            // Final selection weight.
            double weight = priority * overdueBoost;

            weight = Math.max(weight, 0.1);

            weights.add(weight);

            totalWeight += weight;
        }

        // STEP 6: Weighted random selection.
        double randomValue =
                random.nextDouble() * totalWeight;

        double cumulativeWeight = 0.0;

        for (int i = 0; i < candidates.size(); i++) {

            cumulativeWeight += weights.get(i);

            if (randomValue < cumulativeWeight) {
                return candidates.get(i);
            }
        }

        // Fallback for floating-point rounding.
        return candidates.get(candidates.size() - 1);
    }

    /**
     * Gets the next scheduled review date.
     */
    private LocalDateTime getNextReviewAt(
            Word word,
            Map<Word, WordLearningStats> stats) {

        // Use the schedule saved in PostgreSQL.
        if (word.getNextReviewAt() != null) {
            return word.getNextReviewAt();
        }

        // Otherwise calculate a fallback schedule.
        WordLearningStats wordStats = stats.get(word);

        return scheduleService.calculateNextReviewAt(wordStats);
    }
}
