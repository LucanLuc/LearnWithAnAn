package test;

import java.util.ArrayList;
import java.util.List;

import model.Word;
import repository.WordRepositoryInterface;

public class InMemoryWordRepository implements WordRepositoryInterface {

    private List<Word> words = new ArrayList<>();

    @Override
    public List<Word> loadWords() {
        return new ArrayList<>(words);
    }

    @Override
    public void saveWord(Word word) {
        words.add(word);
    }

    @Override
    public void deleteWord(Word word) {
        words.remove(word);
    }

    @Override
    public void updateWord(String oldWord, Word word) {
        // The stored object is already updated in memory.
    }

    @Override
    public void updateReviewStats(Word word) {
        // Statistics are stored directly in the Word object.
    }

    @Override
    public void updateImagePath(Word word) {
        // Image path is stored directly in the Word object.
    }
}
