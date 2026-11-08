package repository;


import java.util.List;
import model.Word;

public interface WordRepositoryInterface {

    List<Word> loadWords();

    void saveWord(Word word);

    void deleteWord(Word word);

    void updateWord(String oldWord, Word word);

    void updateReviewStats(Word word);

    void updateImagePath(Word word);
}
