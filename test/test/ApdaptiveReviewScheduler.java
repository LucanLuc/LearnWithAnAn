package test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Backend.AdaptiveReviewScheduler;
import model.ReviewRecord;
import model.Word;

public class AdaptiveReviewSchedulerTest {

    private static final AdaptiveReviewScheduler s = new AdaptiveReviewScheduler();
    private static final Word w = new Word("a", "b", LocalDateTime.now(), 0, 0);
    private static final LocalDateTime t0 = LocalDateTime.of(2026, 1, 1, 9, 0);

    public static void main(String[] args) {
        newWordIsDueNow();
        intervalsGrowOnSuccess();
        wrongAnswerComesBackSoonNotImmediately();
        easeDropsAfterLapseAndRecovers();
        lateCorrectGetsLongerInterval();
        intervalIsCapped();
        System.out.println("\nAll adaptive scheduler tests passed!");
    }

    private static List<ReviewRecord> history(LocalDateTime start, long gapDays, boolean... answers) {
        List<ReviewRecord> list = new ArrayList<>();
        LocalDateTime at = start;
        for (boolean a : answers) {
            list.add(new ReviewRecord(w, a, at));
            at = at.plusDays(gapDays);
        }
        return list;
    }

    private static void newWordIsDueNow() {
        LocalDateTime now = LocalDateTime.now();
        check(s.calculate(new ArrayList<>(), now).nextReviewAt.equals(now), "new word due now");
    }

    private static void intervalsGrowOnSuccess() {
        double i1 = s.calculate(history(t0, 1, true), t0).intervalDays;
        double i2 = s.calculate(history(t0, 1, true, true), t0).intervalDays;
        double i3 = s.calculate(history(t0, 3, true, true, true), t0).intervalDays;
        check(i1 == 1.0 && i2 == 3.0, "1d then 3d");
        check(i3 > i2, "third interval larger: " + i3);
    }

    private static void wrongAnswerComesBackSoonNotImmediately() {
        AdaptiveReviewScheduler.Schedule sch = s.calculate(history(t0, 1, true, false), t0);
        LocalDateTime wrongAt = t0.plusDays(1);
        check(sch.nextReviewAt.equals(wrongAt.plus(AdaptiveReviewScheduler.RELEARN_DELAY)),
                "wrong answer relearn delay");
        check(sch.lapses == 1, "lapse counted");
    }

    private static void easeDropsAfterLapseAndRecovers() {
        double good = s.calculate(history(t0, 1, true, true, true), t0).easeFactor;
        double lapsed = s.calculate(history(t0, 1, true, true, false), t0).easeFactor;
        check(lapsed < good, "ease drops after lapse");
        double floor = s.calculate(history(t0, 1, false, false, false, false, false, false, false, false), t0).easeFactor;
        check(floor >= AdaptiveReviewScheduler.MIN_EASE, "ease floor");
    }

    private static void lateCorrectGetsLongerInterval() {
        double onTime = s.calculate(history(t0, 3, true, true, true), t0).intervalDays;
        double late = s.calculate(history(t0, 20, true, true, true), t0).intervalDays;
        check(late > onTime, "late recall rewarded: " + late + " > " + onTime);
    }

    private static void intervalIsCapped() {
        boolean[] a = new boolean[40];
        java.util.Arrays.fill(a, true);
        double d = s.calculate(history(t0, 30, a), t0).intervalDays;
        check(d <= AdaptiveReviewScheduler.MAX_INTERVAL_DAYS, "interval cap");
    }

    private static void check(boolean ok, String msg) {
        if (!ok) throw new AssertionError("FAILED: " + msg);
        System.out.println("PASS: " + msg);
    }
}
