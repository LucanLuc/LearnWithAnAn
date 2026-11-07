package Backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class WordRepository {

    // Save ONE word to PostgreSQL
    public void saveWord(Word word) {

        String sql = """
                INSERT INTO words (word, definition, date_added, correct_count, wrong_count)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, word.getWord());
            pstmt.setString(2, word.getDefinition());
            pstmt.setObject(3, word.getDateAdded());
            pstmt.setInt(4,  word.getCorrectCount());
            pstmt.setInt(5,  word.getIncorrectCount());

            pstmt.executeUpdate();

            System.out.println("Word saved successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Load ALL words from PostgreSQL
    public List<Word> loadWords() {

        List<Word> words = new ArrayList<>();

        String sql = """
                SELECT word, definition, date_added, correct_count, wrong_count
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
                java.time.LocalDateTime dateAdded =
                        timestamp.toLocalDateTime();
                
                int correctCount =rs.getInt("correct_count"); 
                int wrongCount = rs.getInt("wrong_count"); 

                Word word = new Word(
                        wordText,
                        definition,
                        dateAdded,
                        correctCount,
                        wrongCount
                );

                words.add(word);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return words;
    }
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

            System.out.println("Word deleted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateWord(String oldWord, Word word) {

        String sql = """
                UPDATE words
                SET word = ?, definition = ?
                WHERE word = ?
                """;

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, word.getWord());
            pstmt.setString(2, word.getDefinition());
            pstmt.setString(3, oldWord);

            pstmt.executeUpdate();

            System.out.println("Word updated successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateReviewStats(Word word) {
    	String sql = """
    			UPDATE words
    			SET correct_count = ?, 
    					wrong_count =?
    			WHERE word = ?
    			"""; 
    	try (
    		Connection conn = DatabaseConnection.getConnection();
    		PreparedStatement pstmt = conn.prepareStatement(sql)
    	)  {
    		pstmt.setInt(1, word.getCorrectCount());
    		pstmt.setInt(2, word.getIncorrectCount()); 
    		pstmt.setString(3, word.getWord());
    		
    		pstmt.executeUpdate(); 
    		
    		System.out.println("Review statistics updated!" ); 
    	} catch (SQLException e) 
    	{
    		e.printStackTrace();
    		}
    	}
}