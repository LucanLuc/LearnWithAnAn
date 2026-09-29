package Backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReviewHistoryRepository {

    // Save one review record to PostgreSQL
    public void saveReview(ReviewRecord record) {

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
            pstmt.setObject(3, record.getReviewedAt());

            pstmt.executeUpdate();

            System.out.println("Review history saved successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Load all review records from PostgreSQL
    public List<ReviewRecord> loadReviews(List<Word> words) {

        List<ReviewRecord> records = new ArrayList<>();

        String sql = """
                SELECT word, correct, reviewed_at
                FROM review_history
                ORDER BY reviewed_at
                """;

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {

            while (rs.next()) {

                String wordText = rs.getString("word");
                boolean correct = rs.getBoolean("correct");

                Timestamp timestamp =
                    rs.getTimestamp("reviewed_at");

                LocalDateTime reviewedAt =
                    timestamp.toLocalDateTime();

                Word matchedWord = null;

                for (Word word : words) {

                    if (word.getWord().equals(wordText)) {
                        matchedWord = word;
                        break;
                    }
                }

                if (matchedWord != null) {

                    ReviewRecord record =
                        new ReviewRecord(
                            matchedWord,
                            correct, 
                            reviewedAt
                        );

                    records.add(record);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }
}