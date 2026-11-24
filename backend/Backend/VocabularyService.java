package Backend;

import java.time.LocalDateTime;
import ai.AIStudyContentService;
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
import ai.PersonalizedExerciseService;
import model.AIGeneratedWordContent;

import java.util.concurrent.CompletableFuture; 
import java.util.concurrent.CompletionException; 

public class VocabularyService {
	private Dictionary dictionary;
	private Review review; 
	private ReviewHistory reviewHistory; 
	private ReviewHistoryRepository reviewHistoryRepository; 
	private LearningStatsService learningStatsService; 
	private AIImageService aiImageService; 
	private AIStudyContentService aiStudyContentService;
	private ReviewSubmissionRepository reviewSubmissionRepository; 
	private ReviewScheduleService reviewScheduleService; 
	private PersonalizedExerciseService exerciseService;
	private AIExerciseTracker aiExerciseTracker =
	        new AIExerciseTracker();
	
	public VocabularyService() {
		dictionary= new Dictionary(); 
		review = new Review(dictionary);
		
		aiImageService = new AIImageService(); 
		learningStatsService = new LearningStatsService(); 
		reviewHistory = new ReviewHistory(); 
		aiStudyContentService = new AIStudyContentService();
		reviewHistoryRepository = new ReviewHistoryRepository(); 
		reviewSubmissionRepository = new ReviewSubmissionRepository(); 
		reviewScheduleService = new ReviewScheduleService(); 
		exerciseService = new PersonalizedExerciseService();
		
		List<ReviewRecord> savedReviews = reviewHistoryRepository.loadReviews(dictionary.getWords()); 
	
		for (ReviewRecord record : savedReviews) {
			reviewHistory.addResult(record);
		}
	}
	
	public void recordAIExerciseResult(
	        model.AIExerciseResult result) {

	    aiExerciseTracker.recordResult(result);
	}

	public AIExerciseTracker getAIExerciseTracker() {
	    return aiExerciseTracker;
	}
	
	// Generate an image preview without saving the word
	public String generateWordImage(
	        String word,
	        AIGeneratedWordContent content
	) throws Exception {

	    if (word == null || word.isBlank()) {
	        throw new IllegalArgumentException(
	                "Word cannot be empty."
	        );
	    }

	    if (content == null) {
	        throw new IllegalArgumentException(
	                "Generated content cannot be empty."
	        );
	    }

	    // Create a temporary Word object for image generation
	    Word temporaryWord = new Word(
	            word.trim(),
	            content.getSimpleExplanation(),
	            LocalDateTime.now(),
	            0,
	            0
	    );

	    // Generate and save the image locally
	    return aiImageService.generateImage(temporaryWord);
	}
	
	public String generatePersonalizedExercise(
	        Word word,
	        WordLearningStats stats) throws Exception {

	    return exerciseService.generateExercise(word, stats);
	}
	
	public Map<Word, WordLearningStats> getLearningStats() {
		return learningStatsService.calculateStats(dictionary.getWords(), reviewHistory); 
	}
	
	public String generateStudyContent(Word word) throws Exception {
	    return aiStudyContentService.generateLearningContent(word);
	}
	
	// Generate translation, explanation, and example for a new word
	public AIGeneratedWordContent generateWordDetails(String word)
	        throws Exception {

	    if (word == null || word.isBlank()) {
	        throw new IllegalArgumentException(
	                "Please enter a vocabulary word."
	        );
	    }

	    return aiStudyContentService.generateVocabularyContent(word);
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
	
	
	// Save a word after the user confirms the AI-generated content
	// Save generated vocabulary and reuse an existing image if available
	public void saveGeneratedWord(
	        String word,
	        AIGeneratedWordContent content
	) {
	    saveGeneratedWord(word, content, null);
	}


	// Save vocabulary with an optional generated image path
	// Existing method: preserve compatibility with other parts of the app
	public void saveGeneratedWord(
	        String word,
	        AIGeneratedWordContent content
	) {
	    saveGeneratedWord(word, content, null, true);
	}


	// Existing three-argument version
	public void saveGeneratedWord(
	        String word,
	        AIGeneratedWordContent content,
	        String imagePath
	) {
	    saveGeneratedWord(word, content, imagePath, true);
	}


	// Main save method
	public void saveGeneratedWord(
	        String word,
	        AIGeneratedWordContent content,
	        String imagePath,
	        boolean generateImageIfMissing
	) {

	    if (word == null || word.isBlank()) {
	        throw new IllegalArgumentException("Word cannot be empty.");
	    }

	    if (content == null) {
	        throw new IllegalArgumentException("Generated content cannot be empty.");
	    }

	    String cleanWord = word.trim();

	    if (dictionary.searchWord(cleanWord) != null) {
	        throw new IllegalArgumentException(
	                "This word already exists in your dictionary."
	        );
	    }

	    Word newWord = new Word(
	            cleanWord,
	            content.getSimpleExplanation(),
	            LocalDateTime.now(),
	            0,
	            0
	    );

	    newWord.setTranslation(content.getTranslation());
	    newWord.setSimpleExplanation(content.getSimpleExplanation());
	    newWord.setExampleSentence(content.getExampleSentence());

	    if (imagePath != null && !imagePath.isBlank()) {
	        newWord.setImagePath(imagePath);
	    }

	    dictionary.addWord(newWord);

	    if (imagePath != null && !imagePath.isBlank()) {
	        dictionary.updateImagePath(newWord);
	    } else if (generateImageIfMissing) {
	        generateImageForWord(newWord);
	    }
	}
	// Update the saved word when its image finishes generating
	public void updateGeneratedImage(
	        String word,
	        String imagePath
	) {

	    Word savedWord = dictionary.searchWord(word);

	    if (savedWord != null) {

	        savedWord.setImagePath(imagePath);

	        dictionary.updateImagePath(savedWord);

	    }
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


