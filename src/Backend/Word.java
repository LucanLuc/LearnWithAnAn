package Backend;
import java.util.*; 
import java.time.LocalDateTime; 

public class Word {
	private String word; 
	private String definition; 
	private Character difficulty; 
	private LocalDateTime dateAdded; 

	
	public Word (String word, String definition, LocalDateTime time) {
		this.word = word; 
		this.definition = definition; 
		this.dateAdded = time; 
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
