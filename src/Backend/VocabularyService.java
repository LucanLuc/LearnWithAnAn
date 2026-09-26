package Backend;

import java.time.LocalDateTime;
import java.util.List; 

public class VocabularyService {
	private Dictionary dictionary;
	private Review review; 
	
	public VocabularyService() {
		dictionary= new Dictionary(); 
		review = new Review(dictionary); 
	}
	
	public void addWord(String word, String definition) {
		Word newWord = new Word(word, definition, LocalDateTime.now()); 
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
		return review.nextQuestion(); 
	}
	public List<String> getReviewChoices() {
	    return review.getCurrentChoices();
	}
	public ReviewResult answerReview(String answer) {
	    return review.checkAnswer(answer);
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
	
}


