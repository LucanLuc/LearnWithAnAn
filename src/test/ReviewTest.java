package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import Backend.Dictionary;
import Backend.Review;
import Backend.Word;

class ReviewTest {

	@Test
	void correctAnswerShouldIncreaseScore() {
		var dictionary = new Dictionary(); 
		Word word = new Word("apple", "a fruit", LocalDateTime.now()); 
		dictionary.addWord(word); 
		
		var review = new Review (dictionary); 
		review.getRandomWord(); 
		boolean result = review.checkAnswer("a fruit"); 
		assertTrue(result); 
		assertEquals(1, review.getNumOfCorrect()); 
	}

}
