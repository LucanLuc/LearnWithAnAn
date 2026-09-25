package Backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Review {

    private Dictionary dictionary;
    private Random random;

    private Word currentWord;
    private Word previousWord; 
    private int numOfCorrect;
    private int numOfIncorrect;
    private List<String> currentChoices;
    

    public Review(Dictionary dictionary) {

        this.dictionary = dictionary;
        this.random = new Random();

        // Make a copy of the dictionary words

        this.numOfCorrect = 0;
        this.numOfIncorrect = 0;
        this.currentWord = null;
    }

    // Get a random word
 // Get a random word
    public Word getRandomWord() {

        List<Word> words = dictionary.getWords();

        if (words.isEmpty()) {
            currentWord = null;
            return null;
        }

        if (words.size() == 1) {
            currentWord = words.get(0);
            return currentWord;
        }

        Word nextWord;

        do {
            nextWord = words.get(
                random.nextInt(words.size())
            );
        } while (nextWord.equals(currentWord));

        currentWord = nextWord;

        return currentWord;
    }
   
    
    // Generate possible definitions
    public List<String> getChoices(Word correctWord) {

        List<String> choices = new ArrayList<>();

        if (correctWord == null) {
            return choices;
        }

        // Add correct answer
        choices.add(correctWord.getDefinition());

        // Create a copy of all words
        List<Word> otherWords = new ArrayList<> (dictionary.getWords()); 

        // Remove the correct word
        otherWords.remove(correctWord);

        // Shuffle the remaining words
        Collections.shuffle(otherWords);

        // Add up to 3 incorrect answers
        for (Word word : otherWords) {

            if (choices.size() >= 4) {
                break;
            }

            choices.add(word.getDefinition());
        }

        // Shuffle all answers
        Collections.shuffle(choices);

        return choices;
    }

    // Check user's answer
    public ReviewResult checkAnswer(String answer) {

        if (currentWord == null || answer == null) {
            return new ReviewResult(false, "");
        }

        boolean correct =
            currentWord.getDefinition()
                       .equalsIgnoreCase(answer.trim());

        if (correct) {
            numOfCorrect++;
        } else {
            numOfIncorrect++;
        }

        return new ReviewResult(
            correct,
            currentWord.getDefinition()
        );
    }
    
    
    public void startReview() {
    	numOfCorrect =0; 
    	numOfIncorrect = 0; 
    	currentWord= null; 
    }

    // Get number of correct answers
    public int getNumOfCorrect() {
        return numOfCorrect;
    }
    private Word getCurrentWord() {
    	return currentWord; 
    }
    
    public Word nextQuestion() {

        getRandomWord();

        if (currentWord != null) {
            currentChoices = getChoices(currentWord);
        } else {
            currentChoices = new ArrayList<>();
        }

        return currentWord;
    }
    public List<String> getCurrentChoices() {
        return currentChoices;
    }
    
    // Get number of incorrect answers
    public int getNumOfIncorrect() {
        return numOfIncorrect;
    }

    // Get total number of questions answered
    public int getTotalQuestions() {
        return numOfCorrect + numOfIncorrect;
    }
    public ReviewSummary getSummary(){
    	return new ReviewSummary(numOfCorrect, numOfIncorrect); 
    	}
 }