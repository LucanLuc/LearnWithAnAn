package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach; 
import Backend.Word;
import Backend.WordRepository;

class WordRepositoryTest {
	@AfterEach
	void cleanup() {
	    WordRepository repository = new WordRepository();

	    repository.deleteWord(new Word(
	            "ephemeral",
	            "",
	            LocalDateTime.now()
	    ));

	    repository.deleteWord(new Word(
	            "testdelete",
	            "",
	            LocalDateTime.now()
	    ));

	    repository.deleteWord(new Word(
	            "testupdate",
	            "",
	            LocalDateTime.now()
	    ));
	}

    @Test
    void saveAndLoadWordTest() {

        WordRepository repository = new WordRepository();

        Word word = new Word(
                "ephemeral",
                "lasting for a very short time",
                LocalDateTime.now()
        );

        // Save to PostgreSQL
        repository.saveWord(word);

        // Load from PostgreSQL
        List<Word> words = repository.loadWords();

        // Check whether the word exists
        boolean found = false;

        for (Word w : words) {
            if (w.getWord().equals("ephemeral")) {
                found = true;
                break;
            }
        }

        assertTrue(found);
    }
    
    @Test
    void deleteWordTest() {

        WordRepository repository = new WordRepository();

        Word word = new Word(
                "testdelete",
                "word that will be deleted",
                LocalDateTime.now()
        );

        // First save it
        repository.saveWord(word);

        // Then delete it
        repository.deleteWord(word);

        // Load everything
        List<Word> words = repository.loadWords();

        // Check that it is gone
        boolean found = false;

        for (Word w : words) {
            if (w.getWord().equals("testdelete")) {
                found = true;
                break;
            }
        }

        assertFalse(found);
    }
    @Test
    void updateWordTest() {

        WordRepository repository = new WordRepository();

        Word word = new Word(
                "testupdate",
                "old definition",
                LocalDateTime.now()
        );

        repository.saveWord(word);

        Word updatedWord = new Word(
                "testupdate",
                "new definition",
                word.getDateAdded()
        );

        repository.updateWord("testupdate", updatedWord);

        List<Word> words = repository.loadWords();

        String definition = null;

        for (Word w : words) {
            if (w.getWord().equals("testupdate")) {
                definition = w.getDefinition();
                break;
            }
        }

        assertEquals("new definition", definition);
    }
}