package Backend;

import java.util.List;
import java.util.Map;

public class IntelligentWordSelector {

    private ReviewPriorityService priorityService;

    public IntelligentWordSelector() {

        priorityService =
            new ReviewPriorityService();
    }

    public Word chooseWord(
            List<Word> words,
            Map<Word, WordLearningStats> stats,
            Word previousWord) {

        if (words.isEmpty()) {
            return null;
        }

        // If there is only one word,
        // we have no choice but to show it again.
        if (words.size() == 1) {
            return words.get(0);
        }

        Word bestWord = null;
        double highestPriority = -1;

        for (Word word : words) {

            // Don't show the same word twice in a row
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
                bestWord = word;
            }
        }

        return bestWord;
    }
}