package Backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Collections;

import model.ReviewRecord;

public class ReviewHistory {

    private List<ReviewRecord> results;

    public ReviewHistory() {
        results = new ArrayList<>();
    }

    public void addResult(ReviewRecord result) {
        results.add(result);
    }

    public List<ReviewRecord> getResults() {
        return Collections.unmodifiableList(results);
    }

    public int getTotalReviews() {
        return results.size();
    }

    public int getCorrectReviews() {

        int correct = 0;

        for (ReviewRecord result : results) {

            if (result.isCorrect()) {
                correct++;
            }
        }

        return correct;
    }

    public int getIncorrectReviews() {

        int incorrect = 0;

        for (ReviewRecord result : results) {

            if (!result.isCorrect()) {
                incorrect++;
            }
        }

        return incorrect;
    }
}