
package ai;

import Backend.WordLearningStats;
import model.Word;

public class PersonalizedExerciseService {

    private final AITextClient textClient;

    public PersonalizedExerciseService() {
        textClient = new AITextClient();
    }

    public String generateExercise(
            Word word,
            WordLearningStats stats) throws Exception {

        if (word == null) {
            throw new IllegalArgumentException(
                    "Word cannot be null."
            );
        }

        String learningState = "NEW";
        double accuracy = 0;
        double recentAccuracy = 0;
        int incorrectReviews = 0;
        int correctStreak = 0;
        int totalReviews = 0;

        if (stats != null) {
            learningState = stats.getLearningState().name();
            accuracy = stats.getAccuracy();
            recentAccuracy = stats.getRecentAccuracy(3);
            incorrectReviews = stats.getIncorrectReviews();
            correctStreak = stats.getCorrectStreak();
            totalReviews = stats.getTotalReviews();
        }

        String prompt = """
                You are an adaptive English vocabulary tutor.

                Create ONE personalized exercise for this learner.

                TARGET WORD: %s
                DEFINITION: %s

                LEARNER PERFORMANCE:
                Learning state: %s
                Overall accuracy: %.1f%%
                Recent accuracy: %.1f%%
                Incorrect answers: %d
                Correct streak: %d
                Total reviews: %d

                ADAPTATION RULES:

                1. If the learner is NEW or has no review history:
                   Create a simple meaning recognition question.

                2. If the learner is WEAK or has low accuracy:
                   Use a clear sentence completion question with
                   enough context to understand the answer.

                3. If the learner is LEARNING:
                   Test the word in a realistic sentence.

                4. If the learner is FAMILIAR:
                   Test a more nuanced use of the word.

                5. If the learner is MASTERED and has a strong
                   correct streak:
                   Create a challenging contextual question.

                EXERCISE FORMAT:

                QUESTION:
                Write one question.

                A:
                Option A

                B:
                Option B

                C:
                Option C

                D:
                Option D

                CORRECT ANSWER:
                Write exactly one letter: A, B, C, or D.

                EXPLANATION:
                Explain why the answer is correct.

                DIFFICULTY:
                Beginner, Intermediate, or Advanced.

                IMPORTANT:
                - Only one option must be correct.
                - Make all four options plausible.
                - Use natural English.
                - Test the target word, not unrelated vocabulary.
                - Do not reveal the correct answer in the question.
                - Keep the explanation concise.
                """.formatted(
                    word.getWord(),
                    word.getDefinition(),
                    learningState,
                    accuracy,
                    recentAccuracy,
                    incorrectReviews,
                    correctStreak,
                    totalReviews
                );

        return textClient.generateText(prompt);
    }
}
