package Backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class IntelligentWordSelector {

    private ReviewPriorityService priorityService;
    private Random random;

    public IntelligentWordSelector() {

        priorityService =
            new ReviewPriorityService();

        random = new Random();
    }

    public Word chooseWord(

            List<Word> words,

            Map<Word, WordLearningStats> stats,

            Word previousWord) {

        if (words.isEmpty()) {
            return null;
        }

        // If there is only one word, we have no choice
        if (words.size() == 1) {
            return words.get(0);
        }

        List<Word> candidates = new ArrayList<>();
        List<Double> weights = new ArrayList<>();

        double totalWeight = 0.0;

        // Calculate the weight of every word
        for (Word word : words) {

            // Avoid showing the same word twice in a row
            if (word.equals(previousWord)) {
                continue;
            }

            WordLearningStats wordStats =
                stats.get(word);

            if (wordStats == null) {
                continue;
            }

            double priority =
                priorityService.calculatePriority(
                    wordStats
                );

            /*
             * Priority becomes the probability weight.
             *
             * Higher priority = higher chance
             * of being selected.
             */
            double weight = priority;

            candidates.add(word);
            weights.add(weight);

            totalWeight += weight;
        }

        if (candidates.isEmpty()) {
            return null;
        }

        // Random number between 0 and totalWeight
        double randomValue =
            random.nextDouble() * totalWeight;

        double cumulativeWeight = 0.0;

        // Find which word the random value lands on
        for (int i = 0; i < candidates.size(); i++) {

            cumulativeWeight += weights.get(i);

            if (randomValue < cumulativeWeight) {
                return candidates.get(i);
            }
        }

        // Safety fallback
        return candidates.get(candidates.size() - 1);
    }
}