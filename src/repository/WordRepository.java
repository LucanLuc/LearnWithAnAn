
package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.Word;
import exception.DatabaseOperationException;

public class WordRepository implements WordRepositoryInterface {

    // Convert SQL errors into application-level exceptions
    private DatabaseOperationException databaseError(
        String operation,
        SQLException e
    ) {
        return new DatabaseOperationException(
            "Database operation failed: " + operation,
            e
        );
    }

    // Save ONE word to PostgreSQL
    @Override
    public void saveWord(Word word) {

        String sql = """
            INSERT INTO words
            (word, definition, date_added, correct_count,
             wrong_count, image_path)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, word.getWord());
            pstmt.setString(2, word.getDefinition());
            pstmt.setObject(3, word.getDateAdded());
            pstmt.setInt(4, word.getCorrectCount());
            pstmt.setInt(5, word.getIncorrectCount());
            pstmt.setString(6, word.getImagePath());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw databaseError("saving word", e);
        }
    }

    // Load ALL words from PostgreSQL
    @Override
    public List<Word> loadWords() {

        List<Word> words = new ArrayList<>();

        String sql = """
            SELECT word, definition, date_added,
                   correct_count, wrong_count, image_path
            FROM words
            """;

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {

            while (rs.next()) {

                String wordText = rs.getString("word");
                String definition = rs.getString("definition");

                Timestamp timestamp = rs.getTimestamp("date_added");

                LocalDateTime dateAdded =
                    timestamp != null
                    ? timestamp.toLocalDateTime()
                    : null;

                int correctCount = rs.getInt("correct_count");
                int wrongCount = rs.getInt("wrong_count");

                String imagePath = rs.getString("image_path");

                Word word = new Word(
                    wordText,
                    definition,
                    dateAdded,
                    correctCount,
                    wrongCount
                );

                word.setImagePath(imagePath);

                words.add(word);
            }

        } catch (SQLException e) {
            throw databaseError("loading words", e);
        }

        return words;
    }

    // Delete a word
    @Override
    public void deleteWord(Word word) {

        String sql = """
            DELETE FROM words
            WHERE word = ?
            """;

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, word.getWord());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw databaseError("deleting word", e);
        }
    }

    // Update word information
    @Override
    public void updateWord(String oldWord, Word word) {

        String sql = """
            UPDATE words
            SET word = ?, definition = ?, image_path = ?
            WHERE word = ?
            """;

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, word.getWord());
            pstmt.setString(2, word.getDefinition());
            pstmt.setString(3, word.getImagePath());
            pstmt.setString(4, oldWord);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw databaseError("updating word", e);
        }
    }

    // Update review statistics
    @Override
    public void updateReviewStats(Word word) {

        String sql = """
            UPDATE words
            SET correct_count = ?,
                wrong_count = ?
            WHERE word = ?
            """;

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, word.getCorrectCount());
            pstmt.setInt(2, word.getIncorrectCount());
            pstmt.setString(3, word.getWord());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw databaseError("updating review statistics", e);
        }
    }

    // Update image path
    @Override
    public void updateImagePath(Word word) {

        String sql = """
            UPDATE words
            SET image_path = ?
            WHERE word = ?
            """;

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, word.getImagePath());
            pstmt.setString(2, word.getWord());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw databaseError("updating image path", e);
        }
    }
}
