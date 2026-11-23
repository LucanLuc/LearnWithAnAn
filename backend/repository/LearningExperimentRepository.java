
package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import model.LearningExperimentAttempt;
import model.ExperimentParticipant;
import model.ExperimentSession;
import model.LearningExperimentAttempt;
import model.ExperimentStrategy;

public class LearningExperimentRepository {

    // 1. Save a participant and their assigned strategy
    public void saveParticipant(ExperimentParticipant participant)
            throws SQLException {

        String sql = """
            INSERT INTO learning_experiment_participants
                (participant_id, assigned_strategy)
            VALUES (?, ?)
            ON CONFLICT (participant_id) DO NOTHING
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, participant.getParticipantId());
            stmt.setString(2, participant.getAssignedStrategy().name());

            stmt.executeUpdate();
        }
    }

    // 2. Create a new experiment session
    public long createSession(UUID participantId) throws SQLException {

        String sql = """
            INSERT INTO learning_experiment_sessions
                (participant_id)
            VALUES (?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setObject(1, participantId);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }

        throw new SQLException("Could not create experiment session.");
    }

    // 3. Save an individual learning attempt

	public void saveAttempt(long sessionId,
	                        LearningExperimentAttempt attempt)
	        throws SQLException {
	
	    String sql = """
	        INSERT INTO learning_experiment_attempts
	            (session_id, word, is_correct,
	             delayed_retest, response_time_ms, attempted_at)
	        SELECT s.session_id, ?, ?, ?, ?, ?
	        FROM learning_experiment_sessions s
	        JOIN learning_experiment_participants p
	            ON s.participant_id = p.participant_id
	        WHERE s.session_id = ?
	          AND p.assigned_strategy = ?
	        """;
	
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	
	        stmt.setString(1, attempt.getWord());
	        stmt.setBoolean(2, attempt.isCorrect());
	        stmt.setBoolean(3, attempt.isDelayedRetest());
	        stmt.setLong(4, attempt.getResponseTimeMillis());
	        stmt.setTimestamp(
	            5,
	            Timestamp.valueOf(attempt.getAttemptedAt())
	        );
	        stmt.setLong(6, sessionId);
	        stmt.setString(7, attempt.getStrategy().name());
	
	        int rowsInserted = stmt.executeUpdate();
	
	        if (rowsInserted == 0) {
	            throw new SQLException(
	                "Attempt was not saved: session or assigned strategy mismatch."
	            );
	        }
	    }
	}


    // 4. Finish an experiment session
    public void finishSession(long sessionId) throws SQLException {

        String sql = """
            UPDATE learning_experiment_sessions
            SET ended_at = CURRENT_TIMESTAMP,
                status = 'COMPLETED'
            WHERE session_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, sessionId);
            stmt.executeUpdate();
        }
    }

    // 5. Count participants assigned to each strategy
    public Map<ExperimentStrategy, Integer> getStrategyCounts()
            throws SQLException {

        Map<ExperimentStrategy, Integer> counts =
                new EnumMap<>(ExperimentStrategy.class);

        for (ExperimentStrategy strategy : ExperimentStrategy.values()) {
            counts.put(strategy, 0);
        }

        String sql = """
            SELECT assigned_strategy, COUNT(*)
            FROM learning_experiment_participants
            GROUP BY assigned_strategy
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ExperimentStrategy strategy =
                        ExperimentStrategy.valueOf(rs.getString(1));

                counts.put(strategy, rs.getInt(2));
            }
        }

        return counts;
    }
}
