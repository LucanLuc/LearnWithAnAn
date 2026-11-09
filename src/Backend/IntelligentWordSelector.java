
package Backend;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import model.Word;

public class IntelligentWordSelector {

    private ReviewPriorityService priorityService;
    private ReviewScheduleService scheduleService;
    private Random random;

    public IntelligentWordSelector() {

        priorityService = new ReviewPriorityService();
        scheduleService = new ReviewScheduleService();
        random = new Random();
    }

    public Word chooseWord(
            List<Word> words,
            Map<Word, WordLearningStats> stats,
            Word previousWord) {

        if (words == null || words.isEmpty()) {
            return null;
        }

        if (words.size() == 1) {
            return words.get(0);
        }

        LocalDateTime now = LocalDateTime.now();

        List<Word> dueWords = new ArrayList<>();

        // 1. Find all words that are due.
        for (Word word : words) {

            LocalDateTime nextReviewAt =
                    getNextReviewAt(word, stats);

            if (!nextReviewAt.isAfter(now)) {
                dueWords.add(word);
            }
        }

        List<Word> candidates;

        if (!dueWords.isEmpty()) {

            candidates = dueWords;

        } else {

            // 2. If nothing is due, select words
            // with the earliest upcoming review date.

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

        // 3. Avoid immediately repeating the previous word.
        if (candidates.size() > 1) {
            candidates.remove(previousWord);
        }

        // 4. Calculate selection weights.
        List<Double> weights = new ArrayList<>();

        double totalWeight = 0.0;

        for (Word word : candidates) {

            WordLearningStats wordStats = stats.get(word);

            double priority = 1.0;

            if (wordStats != null) {
                priority = priorityService.calculatePriority(
                        wordStats
                );
            }

            priority = Math.max(0.1, priority);

            LocalDateTime nextReviewAt =
                    getNextReviewAt(word, stats);

            double overdueBoost = 1.0;

            if (nextReviewAt.isBefore(now)) {

                long overdueDays = Duration.between(
                        nextReviewAt,
                        now
                ).toDays();

                overdueBoost += Math.min(overdueDays, 30) * 0.1;
            }

            double weight = priority * overdueBoost;

            weights.add(weight);
            totalWeight += weight;
        }

        // 5. Weighted random selection.
        double randomValue =
                random.nextDouble() * totalWeight;

        double cumulativeWeight = 0.0;

        for (int i = 0; i < candidates.size(); i++) {

            cumulativeWeight += weights.get(i);

            if (randomValue < cumulativeWeight) {
                return candidates.get(i);
            }
        }

        return candidates.get(candidates.size() - 1);
    }

    private LocalDateTime getNextReviewAt(
            Word word,
            Map<Word, WordLearningStats> stats) {

        // Prefer the date loaded from PostgreSQL.
        if (word.getNextReviewAt() != null) {
            return word.getNextReviewAt();
        }

        // Fallback for new words without a saved schedule.
        WordLearningStats wordStats = stats.get(word);

        return scheduleService.calculateNextReviewAt(wordStats);
    }
}
