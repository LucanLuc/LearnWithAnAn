package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import Backend.Dictionary;
import Backend.Word;

class DictionaryTest {
	
	@Test
	void shouldReturnNullWhenWordDoesNotExist() {
		var dictionary = new Dictionary()
; 
		Word result= dictionary.getWord("iouesfheifuh"); 
		assertNull(result); 
		}
	@Test
	void getWordShouldFindExistingWord() {
		var dictionary = new Dictionary(); 
		Word word = new Word("apple", "a fruit", LocalDateTime.now());
		dictionary.addWord(word);
		
		Word result = dictionary.getWord("apple"); 
		assertEquals("apple", result.getWord());
	}
	
	@Test
	void shouldFindDefinition() {
		var dictionary = new Dictionary();
		Word word = new Word("apple", "fruit", LocalDateTime.now()); 
		
		dictionary.addWord(word);
		String definition= dictionary.findDefinition("apple"); 
		assertEquals("a fruit", definition); 
	}

}
