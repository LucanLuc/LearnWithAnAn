package Backend;
import java.util.*;
import java.time.LocalDateTime;

public class Main {
	public static void main(String[] args) {

	    WordRepository repository = new WordRepository();

	    Word word = new Word(
	            "ephemeral",
	            "lasting for a very short time",
	            LocalDateTime.now()
	    );

	    repository.saveWord(word);

	    System.out.println("Word saved!");
	}
}
