
package test;

import java.time.LocalDateTime;

import Backend.ReviewScheduleService;
import Backend.WordLearningStats;
import model.ReviewRecord;

public class ReviewScheduleServiceTest {

    private static final ReviewScheduleService scheduler =
            new ReviewScheduleService();

    public static void main(String[] args) {

        testNewWord();
        testFirstCorrectAnswer();
        testSecondCorrectAnswer();
        testThirdCorrectAnswer();
        testFourthCorrectAnswer();
        testIncorrectAnswer();

        System.out.println(
                "\nAll scheduling tests passed!"
        );
    }

    private static void testNewWord() {

        WordLearningStats stats =
                new WordLearningStats(null);

        boolean result = scheduler.isDue(stats);

        check(result, "New word should be due immediately.");
    }

    private static void testFirstCorrectAnswer() {

        WordLearningStats stats = createStats(
                true
        );

        LocalDateTime expected =
                stats.getLastReviewedAt().plusDays(1);

        check(
                scheduler.calculateNextReviewAt(stats)
                        .equals(expected),
                "First correct answer should schedule 1 day later."
        );
    }

    private static void testSecondCorrectAnswer() {

        WordLearningStats stats = createStats(
                true, true
        );

        LocalDateTime expected =
                stats.getLastReviewedAt().plusDays(3);

        check(
                scheduler.calculateNextReviewAt(stats)
                        .equals(expected),
                "Second consecutive correct answer should schedule 3 days later."
        );
    }

    private static void testThirdCorrectAnswer() {

        WordLearningStats stats = createStats(
                true, true, true
        );

        LocalDateTime expected =
                stats.getLastReviewedAt().plusDays(7);

        check(
                scheduler.calculateNextReviewAt(stats)
                        .equals(expected),
                "Third consecutive correct answer should schedule 7 days later."
        );
    }

    private static void testFourthCorrectAnswer() {

        WordLearningStats stats = createStats(
                true, true, true, true
        );

        LocalDateTime expected =
                stats.getLastReviewedAt().plusDays(14);

        check(
                scheduler.calculateNextReviewAt(stats)
                        .equals(expected),
                "Fourth consecutive correct answer should schedule 14 days later."
        );
    }

    private static void testIncorrectAnswer() {

        WordLearningStats stats = createStats(
                true, true, false
        );

        check(
                scheduler.isDue(stats),
                "Incorrect answer should make the word due immediately."
        );
    }

    private static WordLearningStats createStats(
            boolean... answers) {

        WordLearningStats stats =
                new WordLearningStats(null);

        LocalDateTime reviewTime =
                LocalDateTime.now().minusDays(10);

        for (boolean correct : answers) {

            stats.addReview(
                    new ReviewRecord(
                            null,
                            correct,
                            reviewTime
                    )
            );

            reviewTime = reviewTime.plusMinutes(1);
        }

        return stats;
    }

    private static void check(
            boolean condition,
            String message) {

        if (!condition) {
            throw new AssertionError(message);
        }

        System.out.println("[PASS] " + message);
    }
}
