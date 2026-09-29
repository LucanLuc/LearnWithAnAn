package Backend;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map; 

public class VocabularyService {
	private Dictionary dictionary;
	private Review review; 
	private ReviewHistory reviewHistory; 
	private ReviewHistoryRepository reviewHistoryRepository; 
	private LearningStatsService learningStatsService; 
	
	public VocabularyService() {
		dictionary= new Dictionary(); 
		review = new Review(dictionary);
		
		learningStatsService = new LearningStatsService(); 
		reviewHistory = new ReviewHistory(); 
		reviewHistoryRepository = new ReviewHistoryRepository(); 
		
		List<ReviewRecord> savedReviews = reviewHistoryRepository.loadReviews(dictionary.getWords()); 
	
		for (ReviewRecord record : savedReviews) {
			reviewHistory.addResult(record);
		}
	}
	
	public Map<Word, WordLearningStats> getLearningStats() {
		return learningStatsService.calculateStats(dictionary.getWords(), reviewHistory); 
	}
	

	
	public void addWord(String word, String definition) {
	    Word newWord = new Word(
	        word,
	        definition,
	        LocalDateTime.now(),
	        0,
	        0
	    );

	    dictionary.addWord(newWord);
	}
	
	public Word searchWord(String word) {
		return dictionary.searchWord(word); 
	}
	
	
	public void deleteWord(Word word) {
		dictionary.deleteWord(word);
	}
	public Review getReview() {
		return review; 
	}
	
	public List<Word> getAllWords() {
		return dictionary.getWords(); 
	}
	
	public void updateWord(Word word, String newWord, String newDefinition) {
	    dictionary.updateWord(
	        word.getWord(),
	        newWord,
	        newDefinition
	    );
	}
	
	public void startReview() {
		review.startReview(); 
	}
	public Word getNextReviewWord() {
	    return review.nextQuestion(reviewHistory);
	}
	
	public List<String> getReviewChoices() {
	    return review.getCurrentChoices();
	}
	public ReviewResult answerReview(String answer) {
	    ReviewResult result = review.checkAnswer(answer); 
	    ReviewRecord record = new ReviewRecord(
	    		result.getWord(), 
	    		result.isCorrect());
	    
	    reviewHistory.addResult(record);
	    reviewHistoryRepository.saveReview(record);
	    return result; 
	}
	public int getReviewCorrect() {
		return review.getNumOfCorrect(); 
	}
	public int getReviewIncorrect() {
		return review.getNumOfIncorrect(); 
	}
	public int getReviewTotal() {
		return review.getTotalQuestions(); 
	}
	public String getReviewCorrectAnswer(Word word) {
	    return word.getDefinition();
	}
	public ReviewSummary getReviewSummary() {
	    return review.getSummary();
	}
	public Word getCurrentReviewWord() {
	    return review.getCurrentWord();
	}
	
	//for stats
	
		public int getTotalCorrect() {
			int total =0; 
			for (Word word : dictionary.getWords()) {
				total += word.getCorrectCount();
			}
			return total; 
		}
		
		public int getTotalIncorrect() {
			int total =0; 
			for (Word word : dictionary.getWords()) {
				total += word.getIncorrectCount(); 
			}
			
			return total; 
		}
		
		public double getAccuracy() {
			int total = getTotalReviews();
			
			if (total ==0) {
				return 0.0;
			}
			
			return (double) getTotalCorrect() / total *100; 
		}
		
		public int getTotalReviews() {
			return getTotalCorrect() + getTotalIncorrect(); 
		}
		
		public ReviewHistory getReviewHistory() {
			return reviewHistory; 
		}
		
}


