package Backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.ReviewResult;
import model.ReviewSummary;
import model.Word;

import java.util.Map; 

public class Review {

    private Dictionary dictionary;
    private Random random;

    private Word currentWord;
    private int numOfCorrect;
    private int numOfIncorrect;
    private List<String> currentChoices;
    private IntelligentWordSelector wordSelector;
    private boolean answered;
    private LearningStatsService statsService;

    public Review(Dictionary dictionary) {

        this.dictionary = dictionary;
        this.random = new Random();

        this.wordSelector = new IntelligentWordSelector();
        this.statsService = new LearningStatsService();

        this.numOfCorrect = 0;
        this.numOfIncorrect = 0;
        this.currentWord = null;
        this.currentChoices = new ArrayList<>();
        this.answered = false;
    }

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
        List<Word> otherWords =
            new ArrayList<>(dictionary.getWords());

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

        if (currentWord == null ||
            answer == null ||
            answer.trim().isEmpty() ||
            answered) {

            return new ReviewResult(currentWord, false);
        }

        boolean correct = currentWord.getDefinition()
                .equalsIgnoreCase(answer.trim());

        answered = true; 
        return new ReviewResult(currentWord, correct); 
    }

    public boolean hasAnsweredCurrentQuestions() {
    	return answered; 
    }
    
    public void startReview() {

        numOfCorrect = 0;
        numOfIncorrect = 0;
        currentWord = null;
        currentChoices = new ArrayList<>();
        answered = false;
    }
    // Get number of correct answers
    public int getNumOfCorrect() {
        return numOfCorrect;
    }

    public Word getCurrentWord() {
        return currentWord;
    }

    public Word nextQuestion(ReviewHistory history) {

        answered = false;

        getIntelligentWord(history);

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

    public ReviewSummary getSummary() {
        return new ReviewSummary(
            numOfCorrect,
            numOfIncorrect
        );
    }
    
    public Word getIntelligentWord(ReviewHistory history) {

        List<Word> words = dictionary.getWords();

        if (words.isEmpty()) {
            currentWord = null;
            return null;
        }

        Map<Word, WordLearningStats> stats =
                statsService.calculateStats(words, history);

        currentWord = wordSelector.chooseWord(
                words,
                stats,
                currentWord
        );

        return currentWord;
    }
      

    public void recordAnswer(ReviewResult result) {

        if (result == null || result.getWord() == null) {
            throw new IllegalArgumentException(
                "Review result cannot be null."
            );
        }

        if (answered) {
            throw new IllegalStateException(
                "This question has already been recorded."
            );
        }

        answered = true;

        if (result.isCorrect()) {
            numOfCorrect++;
            result.getWord().incrementCorrect();
        } else {
            numOfIncorrect++;
            result.getWord().incrementWrong();
        }
    }
}