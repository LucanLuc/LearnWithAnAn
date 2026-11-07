package Backend;
import java.util.*; 
import java.time.LocalDateTime; 

public class Word {
	private String word; 
	private String definition; 
	private Character difficulty; 
	private LocalDateTime dateAdded; 
	
	//stats fields
	private int correctCount; 
	private int wrongCount;

	public void incrementCorrect() {
		correctCount++; 
	}
	
	public void incrementWrong() {
		wrongCount++; 
	}
	
	public int getIncorrectCount() {
		return wrongCount; 
	}
	
	public int getCorrectCount() {
		return correctCount; 
	}
	
	public Word (String word, String definition, LocalDateTime time, int correctCount, int wrongCount) {
		this.word = word; 
		this.definition = definition; 
		this.dateAdded = time; 
		this.correctCount= correctCount; 
		this.wrongCount = wrongCount; 
	}
	public void setWord(String word) {
		this.word = word; 
	}
	public void setDefinition(String definition) {
		this.definition = definition; 
	}
	
	public String getWord() {
		return word; 
	}
	
	public String getDefinition() {
		return definition; 
	}
	public LocalDateTime getDateAdded() {
		return dateAdded; 
	}
}
