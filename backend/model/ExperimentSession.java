
package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ExperimentSession {

    private final long sessionId;
    private final UUID participantId;
    private final ExperimentStrategy strategy;
    private final LocalDateTime startedAt;

    private LocalDateTime endedAt;

    public ExperimentSession(
            long sessionId,
            UUID participantId,
            ExperimentStrategy strategy,
            LocalDateTime startedAt) {

        this.sessionId = sessionId;
        this.participantId = participantId;
        this.strategy = strategy;
        this.startedAt = startedAt;
    }

    public long getSessionId() {
        return sessionId;
    }

    public UUID getParticipantId() {
        return participantId;
    }

    public ExperimentStrategy getStrategy() {
        return strategy;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void finish() {
        endedAt = LocalDateTime.now();
    }
}
