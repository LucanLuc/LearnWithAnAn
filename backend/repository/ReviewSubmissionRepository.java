
package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Supplier;

import exception.DatabaseOperationException;
import model.ReviewRecord;

public class ReviewSubmissionRepository {

    private final Supplier<Connection> connectionProvider;

    public ReviewSubmissionRepository() {
        this(() -> openConnection());
    }

    // Allows a custom connection provider during testing.
    public ReviewSubmissionRepository(
            Supplier<Connection> connectionProvider) {

        this.connectionProvider =
                Objects.requireNonNull(connectionProvider);
    }

    private static Connection openConnection() {

        try {
            return DatabaseConnection.getConnection();

        } catch (SQLException e) {

            throw new DatabaseOperationException(
                    "Could not establish database connection.",
                    e
            );
        }
    }

    public void saveReviewAndUpdateStats(
            ReviewRecord record,
            LocalDateTime nextReviewAt) {

        if (record == null || record.getWord() == null) {
            throw new IllegalArgumentException(
                    "Review record and word cannot be null."
            );
        }

        if (nextReviewAt == null) {
            throw new IllegalArgumentException(
                    "Next review date cannot be null."
            );
        }

        String insertHistorySQL = """
                INSERT INTO review_history (word, correct, reviewed_at)
                VALUES (?, ?, ?)
                """;

        String updateStatsSQL = """
                UPDATE words
                SET correct_count = correct_count + ?,
                    wrong_count = wrong_count + ?,
                    next_review_at = ?
                WHERE word = ?
                """;

        try (Connection conn = connectionProvider.get()) {

            if (conn == null) {
                throw new SQLException(
                        "Connection provider returned null."
                );
            }

            conn.setAutoCommit(false);

            try {

                // 1. Save the review history.
                try (PreparedStatement historyStmt =
                        conn.prepareStatement(insertHistorySQL)) {

                    historyStmt.setString(
                            1, record.getWord().getWord()
                    );

                    historyStmt.setBoolean(
                            2, record.isCorrect()
                    );

                    historyStmt.setTimestamp(
                            3, Timestamp.valueOf(record.getReviewedAt())
                    );

                    historyStmt.executeUpdate();
                }

                // 2. Update statistics and next review date.
                try (PreparedStatement statsStmt =
                        conn.prepareStatement(updateStatsSQL)) {

                    statsStmt.setInt(
                            1, record.isCorrect() ? 1 : 0
                    );

                    statsStmt.setInt(
                            2, record.isCorrect() ? 0 : 1
                    );

                    statsStmt.setTimestamp(
                            3, Timestamp.valueOf(nextReviewAt)
                    );

                    statsStmt.setString(
                            4, record.getWord().getWord()
                    );

                    int updatedRows = statsStmt.executeUpdate();

                    if (updatedRows != 1) {
                        throw new SQLException(
                                "Expected to update one vocabulary word."
                        );
                    }
                }

                // 3. Commit all database changes together.
                conn.commit();

            } catch (SQLException | RuntimeException e) {

                try {
                    conn.rollback();

                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }

        } catch (SQLException e) {

            throw new DatabaseOperationException(
                    "Failed to submit review transaction.",
                    e
            );
        }
    }
}
