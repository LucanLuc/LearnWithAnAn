package test;

import java.util.Map;

import Backend.IntelligentWordSelector;
import Backend.LearningStatsService;
import Backend.VocabularyService;
import Backend.Word;
import Backend.WordLearningStats;
import Backend.ReviewPriorityService;

public class TestWordSelector {

    public static void main(String[] args) {

        VocabularyService service =
            new VocabularyService();
        
        ReviewPriorityService priorityService = new ReviewPriorityService();
        
        LearningStatsService statsService =
            new LearningStatsService();

        IntelligentWordSelector selector =
            new IntelligentWordSelector();

        Map<Word, WordLearningStats> stats =
            statsService.calculateStats(
                service.getAllWords(),
                service.getReviewHistory()
            );

        Word previousWord = null;

        System.out.println("===== WORD SELECTION TEST =====");

        for (int i = 1; i <= 10; i++) {

            Word selectedWord =
                selector.chooseWord(
                    service.getAllWords(),
                    stats,
                    previousWord
                );

            if (selectedWord == null) {
                System.out.println(
                    "No word was selected."
                );
                break;
            }

            WordLearningStats wordStats =
                stats.get(selectedWord);

			System.out.println(
                i + ". "
                + selectedWord.getWord()
                + " | State: "
                + wordStats.getLearningState()
                + " | Priority: " + priorityService.calculatePriority(wordStats)
            );

            previousWord = selectedWord;
        }
    }
}