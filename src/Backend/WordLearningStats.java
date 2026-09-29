package Backend;

import java.time.LocalDateTime;

public class WordLearningStats {
	private Word word; 
	
	private int totalReviews; 
	private int correctReviews; 
	private int incorrectReviews; 
	
	private LocalDateTime lastReviewedAt; 
	
	public WordLearningStats(Word word) {
		this.word= word; 
		this.totalReviews = 0; 
		this.correctReviews = 0; 
		this.incorrectReviews =0; 
		this.lastReviewedAt =null; 
	}
	
	public void addReview(ReviewRecord record) {
		totalReviews++; 
		
		if (record.isCorrect()) {
			correctReviews++; 
		} else {
			incorrectReviews++; 
		}
		
		if (lastReviewedAt == null || record.getReviewedAt().isAfter(lastReviewedAt)) {
			lastReviewedAt= record.getReviewedAt(); 
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

}
