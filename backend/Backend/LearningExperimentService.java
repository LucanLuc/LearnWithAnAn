
package Backend;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import model.ExperimentParticipant;
import model.ExperimentStrategy;
import model.LearningExperimentAttempt;
import repository.LearningExperimentRepository;

public class LearningExperimentService {

    private final LearningExperimentRepository repository;
    private final ExperimentAssignmentService assignmentService;

    public LearningExperimentService() {
        this.repository = new LearningExperimentRepository();
        this.assignmentService = new ExperimentAssignmentService();
    }

    // 1. Assign a participant to the least-used experiment strategy
    public ExperimentParticipant enrollParticipant()
            throws SQLException {

        Map<ExperimentStrategy, Integer> counts =
                repository.getStrategyCounts();

        ExperimentStrategy strategy =
                assignmentService.assignStrategy(counts);

        ExperimentParticipant participant =
                new ExperimentParticipant(UUID.randomUUID(), strategy);

        repository.saveParticipant(participant);

        return participant;
    }

    // 2. Start a session for an enrolled participant
    public long startSession(ExperimentParticipant participant)
            throws SQLException {

        if (participant == null) {
            throw new IllegalArgumentException(
                    "Participant cannot be null."
            );
        }

        return repository.createSession(
                participant.getParticipantId()
        );
    }

    // 3. Record one learning attempt
    public void recordAttempt(
            long sessionId,
            LearningExperimentAttempt attempt)
            throws SQLException {

        if (attempt == null) {
            throw new IllegalArgumentException(
                    "Attempt cannot be null."
            );
        }

        repository.saveAttempt(sessionId, attempt);
    }

    // 4. Finish a session
    public void finishSession(long sessionId)
            throws SQLException {

        repository.finishSession(sessionId);
    }
}
