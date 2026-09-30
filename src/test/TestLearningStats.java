package test;

import java.util.Map;

import Backend.LearningStatsService;
import Backend.ReviewPriorityService;
import Backend.VocabularyService;
import Backend.Word;
import Backend.WordLearningStats;


public class TestLearningStats {

    public static void main(String[] args) {

        VocabularyService service =
            new VocabularyService();

        LearningStatsService statsService =
            new LearningStatsService();

        ReviewPriorityService priorityService =
            new ReviewPriorityService();

        Map<Word, WordLearningStats> stats =
            statsService.calculateStats(
                service.getAllWords(),
                service.getReviewHistory()
            );

        for (Word word : service.getAllWords()) {

            WordLearningStats wordStats =
                stats.get(word);

            double priority =
                priorityService.calculatePriority(
                    wordStats
                );

            System.out.println(
                "Word: " + word.getWord()
            );

            System.out.println(
                "State: "
                + wordStats.getLearningState()
            );

            System.out.println(
                "Accuracy: "
                + wordStats.getAccuracy()
                + "%"
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

            System.out.println(
                "Priority: "
                + priority
            );
            
            

            System.out.println("----------------------");
        }
    }
}