
package test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import Backend.Dictionary;
import model.Word;
import repository.WordRepositoryInterface;

@ExtendWith(MockitoExtension.class)
public class DictionaryTest {

    private Dictionary dictionary;

    @Mock
    private WordRepositoryInterface repository;

    @BeforeEach
    void setUp() {
        // Whenever loadWords() is called, return an empty list.
        when(repository.loadWords()).thenReturn(new ArrayList<>());

        // Inject our simulated repository into Dictionary.
        dictionary = new Dictionary(repository);
    }

    @Test
    void addWordShouldWork() {

        Word word = new Word("apple", "a fruit", LocalDateTime.now());

        dictionary.addWord(word);

        // Check that the word was added to Dictionary.
        assertEquals(1, dictionary.getWords().size());

        // Check that the repository was asked to save the word.
        verify(repository).saveWord(word);
    }

    @Test
    void searchWordShouldBeCaseInsensitive() {

        Word word = new Word("apple", "a fruit", LocalDateTime.now());

        dictionary.addWord(word);

        assertNotNull(dictionary.searchWord("APPLE"));
        assertEquals("apple", dictionary.searchWord("APPLE").getWord());
    }

    @Test
    void searchWordShouldReturnNullWhenNotFound() {

        assertNull(dictionary.searchWord("banana"));
    }

    @Test
    void deleteWordShouldWork() {

        Word word = new Word("apple", "a fruit", LocalDateTime.now());

        dictionary.addWord(word);
        dictionary.deleteWord(word);

        assertEquals(0, dictionary.getWords().size());

        verify(repository).deleteWord(word);
    }

    @Test
    void addWordShouldNotAddToMemoryWhenDatabaseFails() {

        Word word = new Word("apple", "a fruit", LocalDateTime.now());

        doThrow(new RuntimeException("Database error"))
                .when(repository).saveWord(word);

        assertThrows(RuntimeException.class, () -> {
            dictionary.addWord(word);
        });

        assertEquals(0, dictionary.getWords().size());
    }

    @Test
    void deleteWordShouldNotRemoveFromMemoryWhenDatabaseFails() {

        Word word = new Word("apple", "a fruit", LocalDateTime.now());

        dictionary.addWord(word);

        doThrow(new RuntimeException("Database error"))
                .when(repository).deleteWord(word);

        assertThrows(RuntimeException.class, () -> {
            dictionary.deleteWord(word);
        });

        // The word should still exist because deletion failed.
        assertEquals(1, dictionary.getWords().size());
        assertNotNull(dictionary.searchWord("apple"));
    }
}
