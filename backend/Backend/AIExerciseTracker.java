
package Backend;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.AIExerciseResult;
import repository.AIExerciseRepository;

public class AIExerciseTracker {

    private final List<AIExerciseResult> sessionResults =
            new ArrayList<>();

    private final AIExerciseRepository repository;

    public AIExerciseTracker() {
        repository = new AIExerciseRepository();
    }

    public void recordResult(AIExerciseResult result) {

        if (result == null) {
            throw new IllegalArgumentException(
                    "Result cannot be null."
            );
        }

        try {
            repository.save(result);
            sessionResults.add(result);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not save AI exercise result.", e
            );
        }
    }

    public List<AIExerciseResult> getAllResults() {
        return Collections.unmodifiableList(sessionResults);
    }

    public int getTotalExercises() {
        return sessionResults.size();
    }

    public int getCorrectExercises() {

        int correct = 0;

        for (AIExerciseResult result : sessionResults) {
            if (result.isCorrect()) {
                correct++;
            }
        }

        return correct;
    }

    public double getAccuracy() {

        if (sessionResults.isEmpty()) {
            return 0.0;
        }

        return getCorrectExercises() * 100.0
                / sessionResults.size();
    }

    public List<AIExerciseResult> getResultsForWord(String word) {

        try {
            return repository.findByWord(word);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not retrieve AI exercise results.", e
            );
        }
    }

    public double getAccuracyForWord(String word) {

        try {
            return repository.getAccuracyForWord(word);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not calculate word accuracy.", e
            );
        }
    }
}
