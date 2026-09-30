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

        // Calculate the priority of every word
        double highestPriority = -1;

        for (Word word : words) {

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

            if (priority > highestPriority) {
                highestPriority = priority;
            }
        }

        // Find words close to the highest priority
        List<Word> candidates = new ArrayList<>();

        for (Word word : words) {

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

            // Allow words within 20 points of the highest
            if (priority >= highestPriority - 20) {
                candidates.add(word);
            }
        }

        // Randomly choose from the candidates
        if (!candidates.isEmpty()) {
            return candidates.get(
                random.nextInt(candidates.size())
            );
        }

        return null;
    }
}