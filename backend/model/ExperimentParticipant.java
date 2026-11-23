
package model;

import java.util.UUID;

public class ExperimentParticipant {

    private final UUID participantId;
    private final ExperimentStrategy assignedStrategy;

    public ExperimentParticipant(
            UUID participantId,
            ExperimentStrategy assignedStrategy) {

        if (participantId == null || assignedStrategy == null) {
            throw new IllegalArgumentException(
                    "Participant ID and strategy are required."
            );
        }

        this.participantId = participantId;
        this.assignedStrategy = assignedStrategy;
    }

    public UUID getParticipantId() {
        return participantId;
    }

    public ExperimentStrategy getAssignedStrategy() {
        return assignedStrategy;
    }
}
