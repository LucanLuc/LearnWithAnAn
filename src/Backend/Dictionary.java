package Backend;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime; 

public class Dictionary {
	private List<Word> words; 
	private WordRepository repository; 
	
	public Dictionary() {
		repository = new WordRepository(); 
		words = repository.loadWords(); 
	}
	
	public Word searchWord(String word) {
		for (Word w : words) {
			if (w.getWord().equalsIgnoreCase(word)) {
				return w;
			}
		}
		
		return null; 
	}
	public void addWord(Word word) {
		words.add(word);
		repository.saveWord(word);
	}
	
	public void showAll() {
		for (int i= 0; i <words.size(); i ++) {
			System.out.println(words.get(i).getWord() + " | " + words.get(i).getDefinition()); 
		}
	}
	
	public void deleteWord(Word word) {
	    words.remove(word);
	    repository.deleteWord(word);
	}
	
	public Word getWord(String wordToFind) {
		for (Word word: words) {
			if (word.getWord().equalsIgnoreCase(wordToFind)) {
				return word; 
			}
		} return null; 
	}
	
	public List<Word> getWords() {
		return Collections.unmodifiableList(words); 
	}
	/*
	 * this works with file 
	 */
	
	public String findDefinition(String wordToFind) {
        for (Word word : words) {
            if (word.getWord().equalsIgnoreCase(wordToFind)) {
                return word.getDefinition();
            }
        }
        return null;
    }

    public String findWord(String definition) {
        for (Word word : words) {
            if (word.getDefinition().equalsIgnoreCase(definition)) {
                return word.getWord();
            }
        }
        return null;
	}
    
    public List<Word> returnVocabsByDate (LocalDate date)  {
    	List<Word> result = new ArrayList<>(); 
    	for (Word word: words) {
    		if (word.getDateAdded().toLocalDate().equals(date)) {
    			result.add(word); 
    		}
    	}
    	return result; 
    }
    
    public LocalDateTime findDateByWord (Word word) {
    	return word.getDateAdded(); 
    }
    
    public void updateWord(String oldWord, String newWord, String newDefinition) {

        Word word = getWord(oldWord);

        if (word != null) {

            word.setWord(newWord);
            word.setDefinition(newDefinition);

            repository.updateWord(oldWord, word);
        }
    }
    
}
