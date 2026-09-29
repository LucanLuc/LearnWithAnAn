package Backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LearningStatsService {

    public Map<Word, WordLearningStats> calculateStats(
            List<Word> words,
            ReviewHistory history) {

        Map<Word, WordLearningStats> stats = new HashMap<>();

        // Create stats object for every word
        for (Word word : words) {

            stats.put(
                word,
                new WordLearningStats(word)
            );
        }

        // Add every review to the appropriate word
        for (ReviewRecord record : history.getResults()) {

            Word word = record.getWord();

            WordLearningStats wordStats =
                stats.get(word);

            if (wordStats != null) {
                wordStats.addReview(record);
            }
        }

        return stats;
    }
}