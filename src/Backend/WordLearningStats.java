package Backend;

import java.time.LocalDateTime;
import java.util.ArrayList; 
import java.util.List; 

public class WordLearningStats {
	private Word word; 
	
	private int totalReviews; 
	private int correctReviews; 
	private int incorrectReviews; 
	private List<ReviewRecord> reviews; 
	
	private LocalDateTime lastReviewedAt; 
	
	public WordLearningStats(Word word) {
		this.word= word; 
		this.totalReviews = 0; 
		this.correctReviews = 0; 
		this.incorrectReviews =0; 
		this.lastReviewedAt =null; 
		this.reviews = new ArrayList<>(); 
	}
	
	public void addReview(ReviewRecord record) {

	    reviews.add(record);

	    totalReviews++;

	    if (record.isCorrect()) {
	        correctReviews++;
	    } else {
	        incorrectReviews++;
	    }

	    if (lastReviewedAt == null ||
	        record.getReviewedAt().isAfter(lastReviewedAt)) {

	        lastReviewedAt =
	            record.getReviewedAt();
	    }
	}
	
	public Word getWord() {
		return word; 
	}
	
	public int getTotalReviews() {
		return totalReviews; 
	}
	
	public int getCorrectReviews() {
		return correctReviews;
	}
	
	public int getIncorrectReviews() {
		return incorrectReviews;
	}
	
	public LocalDateTime getLastReviewedAt() {
		return lastReviewedAt; 
	}
	
	public double getAccuracy() {
		if (totalReviews ==0) {
			return 0.0; 
		}
		
		return (double) correctReviews / totalReviews * 100; 
	}
	
	public LearningState getLearningState() {
		if (totalReviews ==0) {
			return LearningState.NEW; 
		}
		
		double accuracy = getAccuracy(); 
		
		if (accuracy < 50) {
			return LearningState.WEAK; 
		}
		
		if (accuracy < 80) {
			return LearningState.LEARNING; 
		}
		
		if (accuracy < 95) {
			return LearningState.FAMILIAR; 
		}
		
		return LearningState.MASTERED; 
		
	}
	
	public long getHoursSinceLastReview() {
		if (lastReviewedAt == null) {
			return Long.MAX_VALUE; 
		}
		
		return java.time.Duration.between(lastReviewedAt, java.time.LocalDateTime.now()).toHours();
	}
	public double getRecentAccuracy(int numberOfReviews) {

	    if (reviews.isEmpty()) {
	        return 0.0;
	    }

	    int total = 0;
	    int correct = 0;

	    for (int i = reviews.size() - 1;
	         i >= 0 && total < numberOfReviews;
	         i--) {

	        ReviewRecord record = reviews.get(i);

	        total++;

	        if (record.isCorrect()) {
	            correct++;
	        }
	    }

	    if (total == 0) {
	        return 0.0;
	    }

	    return (double) correct / total * 100;
	}
	
	public int getCorrectStreak() {

	    int streak = 0;

	    for (int i = reviews.size() - 1; i >= 0; i--) {

	        ReviewRecord record = reviews.get(i);

	        if (record.isCorrect()) {
	            streak++;
	        } else {
	            break;
	        }
	    }

	    return streak;
	}

}
