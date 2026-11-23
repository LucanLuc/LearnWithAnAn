
package Backend;

import java.time.LocalDateTime;

import model.Word;
import repository.WordRepository;

public class Main {

    public static void main(String[] args) {

        WordRepository repository = new WordRepository();

        Word word = new Word(
            "ephemeral",
            "lasting for a very short time",
            LocalDateTime.now(),
            0,
            0
        );

        repository.saveWord(word);

        System.out.println("Word saved!");
    }
}
