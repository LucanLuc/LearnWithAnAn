
package Backend;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import model.ExperimentStrategy;

public class ExperimentAssignmentService {

    private final Random random = new Random();

    public ExperimentStrategy assignStrategy(
            Map<ExperimentStrategy, Integer> participantCounts) {

        if (participantCounts == null) {
            throw new IllegalArgumentException(
                    "Participant counts cannot be null."
            );
        }

        int minimum = Integer.MAX_VALUE;

        for (ExperimentStrategy strategy :
                ExperimentStrategy.values()) {

            int count = participantCounts.getOrDefault(
                    strategy, 0
            );

            minimum = Math.min(minimum, count);
        }

        java.util.List<ExperimentStrategy> candidates =
                new java.util.ArrayList<>();

        for (ExperimentStrategy strategy :
                ExperimentStrategy.values()) {

            int count = participantCounts.getOrDefault(
                    strategy, 0
            );

            if (count == minimum) {
                candidates.add(strategy);
            }
        }

        return candidates.get(random.nextInt(candidates.size()));
    }

    public Map<ExperimentStrategy, Integer> createEmptyCounts() {

        Map<ExperimentStrategy, Integer> counts =
                new EnumMap<>(ExperimentStrategy.class);

        for (ExperimentStrategy strategy :
                ExperimentStrategy.values()) {

            counts.put(strategy, 0);
        }

        return counts;
    }
}
