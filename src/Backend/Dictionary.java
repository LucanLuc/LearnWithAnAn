
package Backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

import model.Word;
import repository.WordRepository;
import repository.WordRepositoryInterface;

public class Dictionary {

    private List<Word> words;
    private WordRepositoryInterface repository;

    // Default constructor
    public Dictionary() {
        this(new WordRepository());
    }

    // Dependency Injection
    public Dictionary(WordRepositoryInterface repository) {
        this.repository = repository;
        this.words = repository.loadWords();
    }

    // Search for a word
    public Word searchWord(String word) {

        for (Word w : words) {
            if (w.getWord().equalsIgnoreCase(word)) {
                return w;
            }
        }
        return null;
    }

    // Backward compatibility
    public Word getWord(String wordToFind) {
        return searchWord(wordToFind);
    }

    // Add vocabulary
    public void addWord(Word word) {
        repository.saveWord(word);
        words.add(word); 
    }

    // Delete vocabulary
    public void deleteWord(Word word) {
        repository.deleteWord(word);
        words.remove(word); 
    }

    // Update review statistics
    public void updateReviewStats(Word word) {
        repository.updateReviewStats(word);
    }

    // Display all vocabulary
    public void showAll() {

        for (Word word : words) {

            System.out.println(
                word.getWord() + " | " + word.getDefinition()
            );
        }
    }

    // Return all vocabulary (read-only list)
    public List<Word> getWords() {
        return Collections.unmodifiableList(words);
    }

    // Find definition using word
    public String findDefinition(String wordToFind) {

        Word word = searchWord(wordToFind);

        if (word != null) {
            return word.getDefinition();
        }

        return null;
    }

    // Find word using definition
    public String findWord(String definition) {

        for (Word word : words) {

            if (word.getDefinition().equalsIgnoreCase(definition)) {
                return word.getWord();
            }
        }

        return null;
    }

    // Return vocabulary added on a specific date
    public List<Word> returnVocabsByDate(LocalDate date) {

        List<Word> result = new ArrayList<>();

        for (Word word : words) {

            if (word.getDateAdded() != null &&
                word.getDateAdded().toLocalDate().equals(date)) {

                result.add(word);
            }
        }

        return result;
    }

    // Find date added
    public LocalDateTime findDateByWord(Word word) {
        return word.getDateAdded();
    }

    // Update vocabulary
    public void updateWord(
    	    String oldWord,
    	    String newWord,
    	    String newDefinition
    	) {

    	    Word word = searchWord(oldWord);

    	    if (word == null) {
    	        return;
    	    }

    	    // Preserve old information
    	    String previousWord = word.getWord();
    	    String previousDefinition = word.getDefinition();

    	    // Temporarily modify the object
    	    word.setWord(newWord);
    	    word.setDefinition(newDefinition);

    	    try {

    	        // Update database
    	        repository.updateWord(oldWord, word);

    	    } catch (RuntimeException e) {

    	        // Roll back in-memory changes
    	        word.setWord(previousWord);
    	        word.setDefinition(previousDefinition);

    	        throw e;
    	    }
    	}
    // Update image path
    public void updateImagePath(Word word) {
        repository.updateImagePath(word);
    }
}
