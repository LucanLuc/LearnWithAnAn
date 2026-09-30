package test;

import java.util.Map;

import Backend.IntelligentWordSelector;
import Backend.LearningStatsService;
import Backend.ReviewPriorityService;
import Backend.VocabularyService;
import Backend.Word;
import Backend.WordLearningStats;

public class TestAdaptiveSystem {

    public static void main(String[] args) {

        VocabularyService service =
            new VocabularyService();

        LearningStatsService statsService =
            new LearningStatsService();

        ReviewPriorityService priorityService =
            new ReviewPriorityService();

        IntelligentWordSelector selector =
            new IntelligentWordSelector();

        Map<Word, WordLearningStats> stats =
            statsService.calculateStats(
                service.getAllWords(),
                service.getReviewHistory()
            );

        System.out.println(
            "===== ADAPTIVE SYSTEM TEST ====="
        );

        System.out.println();

        // Show information about every word
        for (Word word : service.getAllWords()) {

            WordLearningStats wordStats =
                stats.get(word);

            double priority =
                priorityService.calculatePriority(
                    wordStats
                );

            System.out.println(
                word.getWord()
                + " | State: "
                + wordStats.getLearningState()
                + " | Accuracy: "
                + wordStats.getAccuracy()
                + "%"
                + " | Priority: "
                + priority
            );
        }

        System.out.println();
        System.out.println(
            "===== SELECTION ====="
        );

        Word previousWord = null;

        for (int i = 1; i <= 20; i++) {

            Word selectedWord =
                selector.chooseWord(
                    service.getAllWords(),
                    stats,
                    previousWord
                );

            if (selectedWord == null) {

                System.out.println(
                    "No word selected."
                );

                break;
            }

            WordLearningStats wordStats =
                stats.get(selectedWord);

            double priority =
                priorityService.calculatePriority(
                    wordStats
                );

            System.out.println(
                i
                + ". "
                + selectedWord.getWord()
                + " | "
                + wordStats.getLearningState()
                + " | Priority: "
                + priority
            );

            previousWord = selectedWord;
        }
    }
}