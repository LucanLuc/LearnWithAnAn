package Backend;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.ReviewRecord;
import model.Word;

/**
 * Section 4 - Experimental evaluation.
 * Simulates a learner with an exponential forgetting curve and compares
 * review strategies given the same number of reviews. All scheduling is
 * evaluated on simulated time, never the wall clock.
 */
public class SchedulerSimulation {

    public enum Strategy { RANDOM, FIXED_INTERVAL, ADAPTIVE }

    public static class Result {
        public final Strategy strategy;
        public final double finalRetention; // mean recall probability at the end
        public final double sessionAccuracy;

        Result(Strategy s, double retention, double accuracy) {
            this.strategy = s;
            this.finalRetention = retention;
            this.sessionAccuracy = accuracy;
        }
    }

    private static class SimWord {
        final Word word;
        double stability = 1.0; // days; grows with successful reviews
        LocalDateTime lastSeen;
        final List<ReviewRecord> history = new ArrayList<>();

        SimWord(Word w, LocalDateTime start) { word = w; lastSeen = start; }

        double recallProbability(LocalDateTime now) {
            double days = Duration.between(lastSeen, now).toMinutes() / 1440.0;
            return Math.exp(-days / stability);
        }
    }

    public Result run(Strategy strategy, int words, int days,
                      int reviewsPerDay, long seed) {

        Random rng = new Random(seed);
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 8, 0);
        AdaptiveReviewScheduler adaptive = new AdaptiveReviewScheduler();

        List<SimWord> pool = new ArrayList<>();
        for (int i = 0; i < words; i++) {
            pool.add(new SimWord(new Word("w" + i, "d" + i, start, 0, 0), start));
        }

        int correct = 0, total = 0;

        for (int d = 0; d < days; d++) {
            LocalDateTime now = start.plusDays(d).plusHours(1);

            for (int k = 0; k < reviewsPerDay; k++) {
                SimWord pick = choose(strategy, pool, now, rng, adaptive);
                double p = pick.recallProbability(now);
                boolean ok = rng.nextDouble() < p;
                total++;
                if (ok) correct++;

                // Recalling a partly forgotten word strengthens it more.
                if (ok) {
                    pick.stability *= 1.0 + 1.2 * (1.0 - p + 0.3);
                } else {
                    pick.stability = Math.max(1.0, pick.stability * 0.5);
                }
                pick.lastSeen = now;
                pick.history.add(new ReviewRecord(pick.word, ok, now));
            }
        }

        LocalDateTime end = start.plusDays(days).plusHours(1);
        double avg = 0;
        for (SimWord sw : pool) avg += sw.recallProbability(end);

        return new Result(strategy, avg / pool.size(),
                total == 0 ? 0 : (double) correct / total);
    }

    private SimWord choose(Strategy s, List<SimWord> pool, LocalDateTime now,
                           Random rng, AdaptiveReviewScheduler adaptive) {

        if (s == Strategy.RANDOM) {
            return pool.get(rng.nextInt(pool.size()));
        }

        SimWord best = null;
        double bestOverdue = Double.NEGATIVE_INFINITY;

        for (SimWord sw : pool) {
            LocalDateTime due;
            if (sw.history.isEmpty()) {
                due = now.minusDays(1);                    // unseen: due now
            } else if (s == Strategy.ADAPTIVE) {
                due = adaptive.calculateNextReviewAt(sw.history);
            } else {
                due = fixedDue(sw);
            }
            // Most overdue first; tiny random tie-break.
            double overdue = Duration.between(due, now).toMinutes() + rng.nextDouble();
            if (overdue > bestOverdue) { bestOverdue = overdue; best = sw; }
        }
        return best;
    }

    /** Same rule as ReviewScheduleService (1/3/7/14 days by streak). */
    private LocalDateTime fixedDue(SimWord sw) {
        int streak = 0;
        for (int i = sw.history.size() - 1; i >= 0 && sw.history.get(i).isCorrect(); i--) {
            streak++;
        }
        if (streak == 0) return sw.lastSeen;
        int days = streak == 1 ? 1 : streak == 2 ? 3 : streak == 3 ? 7 : 14;
        return sw.lastSeen.plusDays(days);
    }

    /** Averages several seeds for stable numbers. */
    public List<Result> compare(int words, int days, int reviewsPerDay, int seeds) {
        List<Result> out = new ArrayList<>();
        for (Strategy s : Strategy.values()) {
            double ret = 0, acc = 0;
            for (int i = 0; i < seeds; i++) {
                Result r = run(s, words, days, reviewsPerDay, 1000L + i);
                ret += r.finalRetention;
                acc += r.sessionAccuracy;
            }
            out.add(new Result(s, ret / seeds, acc / seeds));
        }
        return out;
    }
}
