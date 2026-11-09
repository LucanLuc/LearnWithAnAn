package Backend;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import ai.AIImageService;
import model.LearningState;
import model.ReviewRecord;
import model.ReviewResult;
import model.ReviewSummary;
import model.Word;
import repository.ReviewHistoryRepository;
import repository.ReviewSubmissionRepository;

import java.util.concurrent.CompletableFuture; 
import java.util.concurrent.CompletionException; 

public class VocabularyService {
	private Dictionary dictionary;
	private Review review; 
	private ReviewHistory reviewHistory; 
	private ReviewHistoryRepository reviewHistoryRepository; 
	private LearningStatsService learningStatsService; 
	private AIImageService aiImageService; 
	private ReviewSubmissionRepository reviewSubmissionRepository; 
	private ReviewScheduleService reviewScheduleService; 
	
	public VocabularyService() {
		dictionary= new Dictionary(); 
		review = new Review(dictionary);
		
		aiImageService = new AIImageService(); 
		learningStatsService = new LearningStatsService(); 
		reviewHistory = new ReviewHistory(); 
		reviewHistoryRepository = new ReviewHistoryRepository(); 
		reviewSubmissionRepository = new ReviewSubmissionRepository(); 
		reviewScheduleService = new ReviewScheduleService(); 
		
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
	    
	    generateImageForWord(newWord); 
	}
	
	public Word searchWord(String word) {
		return dictionary.searchWord(word); 
	}
	
	public CompletableFuture<String> addWordWithImage(String word, String definition) {
		Word newWord = new Word(word, definition, LocalDateTime.now(), 0, 0); 
		
		dictionary.addWord(newWord); 
		
		return CompletableFuture.supplyAsync(() -> {
			try {
				String imagePath = aiImageService.generateImage(newWord); 
				
				dictionary.updateImagePath(newWord); 
				return imagePath; 
			} catch (Exception e) {
				throw new CompletionException(e); 
			}
		}); 
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
	
	    if (review.getCurrentWord() == null ||
	        answer == null ||
	        answer.trim().isEmpty() ||
	        review.hasAnsweredCurrentQuestions()) {
	
	        return new ReviewResult(
	            review.getCurrentWord(),
	            false
	        );
	    }
	
	    // 1. Evaluate the answer.
	    ReviewResult result = review.checkAnswer(answer);
	
	    // 2. Create a review record.
	    ReviewRecord record = new ReviewRecord(
	        result.getWord(),
	        result.isCorrect()
	    );
	
	    // 3. Calculate the updated learning statistics.
	    Map<Word, WordLearningStats> stats =
	        learningStatsService.calculateStats(
	            dictionary.getWords(),
	            reviewHistory
	        );
	
	    WordLearningStats wordStats = stats.get(result.getWord());
	
	    if (wordStats == null) {
	        wordStats = new WordLearningStats(result.getWord());
	    }
	
	    // Include the answer we are about to save.
	    wordStats.addReview(record);
	
	    // 4. Calculate the next scheduled review date.
	    LocalDateTime nextReviewAt =
	        reviewScheduleService.calculateNextReviewAt(wordStats);
	
	    // 5. Save everything in one database transaction.
	    reviewSubmissionRepository.saveReviewAndUpdateStats(
	        record,
	        nextReviewAt
	    );
	
	    // 6. Update in-memory review history.
	    result.getWord().setNextReviewAt(nextReviewAt);
	    reviewHistory.addResult(record);
	
	    // 7. Update in-memory session statistics.
	    review.recordAnswer(result);
	
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
		
		public LearningState getLearningState(Word word) {

		    Map<Word, WordLearningStats> stats =
		        learningStatsService.calculateStats(dictionary.getWords(), reviewHistory);

		    WordLearningStats wordStats = stats.get(word);

		    if (wordStats == null) {
		        return LearningState.NEW;
		    }

		    return wordStats.getLearningState();
		}
		
	public void generateImageForWord(Word word) {
		CompletableFuture.runAsync(( )-> {
			try {
				String imagePath = aiImageService.generateImage(word); 
				dictionary.updateImagePath(word); 
				
				System.out.println("Image generated successfully for: " + word.getWord()); 
			} catch (Exception e) {
				System.err.println("Image generation failed for: " + word.getWord()); 
				
				e.printStackTrace(); 
			}
		});
	}
		
}


