
package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import exception.DatabaseOperationException;
import model.ReviewRecord;
import model.Word;

public class ReviewHistoryRepository {

    public void saveReview(ReviewRecord record) {

        if (record == null || record.getWord() == null) {
            throw new IllegalArgumentException(
                    "Review record and its word cannot be null."
            );
        }

        String sql = """
                INSERT INTO review_history (word, correct, reviewed_at)
                VALUES (?, ?, ?)
                """;

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, record.getWord().getWord());
            pstmt.setBoolean(2, record.isCorrect());
            pstmt.setTimestamp(
                    3,
                    Timestamp.valueOf(record.getReviewedAt())
            );

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Failed to save review history.",
                    e
            );
        }
    }

    public List<ReviewRecord> loadReviews(List<Word> words) {

        List<ReviewRecord> records = new ArrayList<>();

        if (words == null || words.isEmpty()) {
            return records;
        }

        // Build a lookup map for vocabulary words.
        Map<String, Word> wordMap = new HashMap<>();

        for (Word word : words) {
            if (word != null && word.getWord() != null) {
                wordMap.put(
                        word.getWord().toLowerCase(Locale.ROOT),
                        word
                );
            }
        }

        String sql = """
                SELECT word, correct, reviewed_at
                FROM review_history
                ORDER BY reviewed_at ASC
                """;

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {

            while (rs.next()) {

                String wordText = rs.getString("word");

                if (wordText == null) {
                    continue;
                }

                Word matchedWord = wordMap.get(
                        wordText.toLowerCase(Locale.ROOT)
                );

                // Ignore records belonging to deleted words.
                if (matchedWord == null) {
                    continue;
                }

                boolean correct = rs.getBoolean("correct");

                Timestamp timestamp = rs.getTimestamp("reviewed_at");

                LocalDateTime reviewedAt =
                        timestamp != null
                        ? timestamp.toLocalDateTime()
                        : null;

                ReviewRecord record = new ReviewRecord(
                        matchedWord,
                        correct,
                        reviewedAt
                );

                records.add(record);
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Failed to load review history.",
                    e
            );
        }

        return records;
    }
}
