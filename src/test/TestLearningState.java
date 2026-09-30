package test;

import java.util.Map;

import Backend.LearningStatsService;
import Backend.VocabularyService;
import Backend.Word;
import Backend.WordLearningStats;

public class TestLearningState {

    public static void main(String[] args) {

        VocabularyService service =
            new VocabularyService();

        LearningStatsService statsService =
            new LearningStatsService();

        Map<Word, WordLearningStats> stats =
            statsService.calculateStats(
                service.getAllWords(),
                service.getReviewHistory()
            );

        System.out.println(
            "===== LEARNING STATE TEST ====="
        );

        for (Word word : service.getAllWords()) {

            WordLearningStats wordStats =
                stats.get(word);

            System.out.println(
                "Word: " + word.getWord()
            );

            System.out.println(
                "Reviews: "
                + wordStats.getTotalReviews()
            );

            System.out.println(
                "Correct: "
                + wordStats.getCorrectReviews()
            );

            System.out.println(
                "Incorrect: "
                + wordStats.getIncorrectReviews()
            );

            System.out.println(
                "Accuracy: "
                + wordStats.getAccuracy()
                + "%"
            );

            System.out.println(
                "State: "
                + wordStats.getLearningState()
            );

            System.out.println(
                "Recent accuracy: "
                + wordStats.getRecentAccuracy(3)
                + "%"
            );

            System.out.println(
                "Correct streak: "
                + wordStats.getCorrectStreak()
            );

            System.out.println("----------------------");
        }
    }
}