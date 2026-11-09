
package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.function.Supplier;

import exception.DatabaseOperationException;
import model.ReviewRecord;

public class ReviewSubmissionRepository {

    private final Supplier<Connection> connectionProvider;

    // Default constructor: use the real database connection.
    public ReviewSubmissionRepository() {
        this(() -> {
			try {
				return DatabaseConnection.getConnection();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		});
    }

    // Constructor for supplying a connection during testing.
    public ReviewSubmissionRepository(Supplier<Connection> connectionProvider) {
        this.connectionProvider = Objects.requireNonNull(connectionProvider);
    }

    public void saveReviewAndUpdateStats(ReviewRecord record) {

        if (record == null || record.getWord() == null) {
            throw new IllegalArgumentException(
                "Review record and word cannot be null."
            );
        }

        String insertHistorySQL = """
            INSERT INTO review_history (word, correct, reviewed_at)
            VALUES (?, ?, ?)
            """;

        String updateStatsSQL = """
            UPDATE words
            SET correct_count = correct_count + ?,
                wrong_count = wrong_count + ?
            WHERE word = ?
            """;

        try (Connection conn = connectionProvider.get()) {

            conn.setAutoCommit(false);

            try {

                // 1. Save review history.
                try (PreparedStatement historyStmt =
                         conn.prepareStatement(insertHistorySQL)) {

                    historyStmt.setString(1, record.getWord().getWord());
                    historyStmt.setBoolean(2, record.isCorrect());
                    historyStmt.setTimestamp(
                        3, Timestamp.valueOf(record.getReviewedAt())
                    );

                    historyStmt.executeUpdate();
                }

                // 2. Update word statistics.
                try (PreparedStatement statsStmt =
                         conn.prepareStatement(updateStatsSQL)) {

                    statsStmt.setInt(1, record.isCorrect() ? 1 : 0);
                    statsStmt.setInt(2, record.isCorrect() ? 0 : 1);
                    statsStmt.setString(3, record.getWord().getWord());

                    int updatedRows = statsStmt.executeUpdate();

                    if (updatedRows != 1) {
                        throw new SQLException(
                            "Expected to update one vocabulary word."
                        );
                    }
                }

                // 3. Commit both operations.
                conn.commit();

            } catch (SQLException | RuntimeException e) {

                // Roll back both operations if either fails.
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
