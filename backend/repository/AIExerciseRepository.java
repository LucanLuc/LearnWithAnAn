
package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.AIExerciseResult;

public class AIExerciseRepository {

    private final DatabaseConnection databaseConnection;

    public AIExerciseRepository() {
        databaseConnection = new DatabaseConnection();
    }

    public void save(AIExerciseResult result) throws SQLException {

        String sql = """
                INSERT INTO ai_exercise_results (
                    word,
                    difficulty,
                    selected_answer,
                    correct_answer,
                    is_correct,
                    completed_at
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, result.getWord());
            statement.setString(2, result.getDifficulty());
            statement.setString(3, result.getSelectedAnswer());
            statement.setString(4, result.getCorrectAnswer());
            statement.setBoolean(5, result.isCorrect());
            statement.setTimestamp(
                    6,
                    Timestamp.valueOf(result.getCompletedAt())
            );

            statement.executeUpdate();
        }
    }

    public List<AIExerciseResult> findByWord(String word)
            throws SQLException {

        String sql = """
                SELECT word, difficulty, is_correct,
                       selected_answer, correct_answer
                FROM ai_exercise_results
                WHERE LOWER(word) = LOWER(?)
                ORDER BY completed_at DESC
                """;

        List<AIExerciseResult> results = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, word);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {

                    results.add(new AIExerciseResult(
                            rs.getString("word"),
                            rs.getString("difficulty"),
                            rs.getBoolean("is_correct"),
                            rs.getString("selected_answer"),
                            rs.getString("correct_answer")
                    ));
                }
            }
        }

        return results;
    }

    public int countExercises() throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM ai_exercise_results
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public double getAccuracyForWord(String word)
            throws SQLException {

        String sql = """
                SELECT
                    COUNT(*) AS total,
                    COUNT(*) FILTER (WHERE is_correct = TRUE) AS correct
                FROM ai_exercise_results
                WHERE LOWER(word) = LOWER(?)
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, word);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {

                    int total = rs.getInt("total");
                    int correct = rs.getInt("correct");

                    if (total == 0) {
                        return 0.0;
                    }

                    return correct * 100.0 / total;
                }
            }
        }

        return 0.0;
    }
}
