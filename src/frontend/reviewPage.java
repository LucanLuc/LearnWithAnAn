package frontend;

import java.awt.*;
import java.util.List;
import javax.swing.*;

import Backend.Word;
import Backend.VocabularyService;
import Backend.ReviewResult;
import Backend.ReviewSummary;

public class reviewPage {

    private JFrame frame;

    private JLabel wordLabel;
    private JLabel scoreLabel;
    private JLabel resultLabel;

    private JButton[] answerButtons;
    private JButton stopButton;
    private Word currentWord;
    private VocabularyService service;

    public reviewPage(VocabularyService service) {
        this.service = service;

        // Start a new review session
        service.startReview();

        // =========================
        // FRAME
        // =========================

        frame = new JFrame("Review");

        Style.stylePage(frame);

        JPanel panel = new JPanel();

        panel.setLayout(
            new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        // =========================
        // TITLE
        // =========================

        JLabel title = new JLabel("Review");

        Style.styleHeading1(title);

        title.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        // =========================
        // SCORE
        // =========================

        scoreLabel = new JLabel("Score: 0");

        scoreLabel.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        // =========================
        // WORD
        // =========================

        wordLabel = new JLabel("Word");

        wordLabel.setFont(
            new Font("Arial", Font.BOLD, 30)
        );

        wordLabel.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        // =========================
        // RESULT
        // =========================

        resultLabel = new JLabel(" ");

        resultLabel.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        // =========================
        // ANSWER BUTTONS
        // =========================

        JPanel answerPanel = new JPanel();

        answerPanel.setLayout(
            new GridLayout(4, 1, 10, 10)
        );

        answerButtons = new JButton[4];

        for (int i = 0; i < 4; i++) {

            answerButtons[i] = new JButton();

            final int index = i;

            answerButtons[i].addActionListener(e -> {
                checkAnswer(index);
            });

            answerPanel.add(answerButtons[i]);
        }

       

        // =========================
        // STOP BUTTON
        // =========================

        stopButton = new JButton("Stop Review");

        stopButton.addActionListener(e -> {
            stopReview();
        });

        // =========================
        // ADD COMPONENTS
        // =========================

        panel.add(Box.createVerticalGlue());

        panel.add(title);

        panel.add(
            Box.createRigidArea(
                new Dimension(0, 20)
            )
        );

        panel.add(scoreLabel);

        panel.add(
            Box.createRigidArea(
                new Dimension(0, 40)
            )
        );

        panel.add(wordLabel);

        panel.add(
            Box.createRigidArea(
                new Dimension(0, 30)
            )
        );

        panel.add(answerPanel);

        panel.add(
            Box.createRigidArea(
                new Dimension(0, 20)
            )
        );

        panel.add(resultLabel);

        panel.add(
            Box.createRigidArea(
                new Dimension(0, 20)
            )
        );


        panel.add(stopButton);

        panel.add(Box.createVerticalGlue());

        // =========================
        // SHOW FRAME
        // =========================

        frame.add(panel);

        frame.setSize(500, 650);

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

        // Start first question
        loadNextQuestion();
    }

    // ==========================================
    // LOAD NEXT QUESTION
    // ==========================================

    private void loadNextQuestion() {

        currentWord = service.getNextReviewWord();

        if (currentWord == null) {

            wordLabel.setText("No words available");

            for (JButton button : answerButtons) {
                button.setEnabled(false);
            }

            panelRefresh();
            return;
        }

        wordLabel.setText(
            currentWord.getWord()
        );

        List<String> choices =
            service.getReviewChoices();

        for (int i = 0; i < answerButtons.length; i++) {

            if (i < choices.size()) {

                answerButtons[i].setText(
                    choices.get(i)
                );

                answerButtons[i].setEnabled(true);
                answerButtons[i].setVisible(true);

            } else {

                answerButtons[i].setVisible(false);
            }
        }

        resultLabel.setText(" ");

        panelRefresh();
    }
    
    // ==========================================
    // CHECK ANSWER
    // ==========================================

    private void checkAnswer(int selectedIndex) {

        String selectedAnswer =
            answerButtons[selectedIndex].getText();

        ReviewResult result =
            service.answerReview(selectedAnswer);
       

        // Disable all answer buttons
        for (JButton button : answerButtons) {
            button.setEnabled(false);
        }

        // Display result
        if (result.isCorrect()) {

            resultLabel.setText(
                "Correct! ✓"
            );

        } else {

            resultLabel.setText(
                "Incorrect. Correct answer: "
                + result.getWord().getDefinition() 
            );
        }

        // Update score
        scoreLabel.setText(
            "Score: "
            + service.getReviewCorrect()
            + " / "
            + service.getReviewTotal()
        );

        panelRefresh();

        // Automatically load the next question after 1 second
        Timer timer = new Timer(1000, e -> {
            loadNextQuestion();
        });

        timer.setRepeats(false);
        timer.start();
    }
    // ==========================================
    // STOP REVIEW
    // ==========================================

    private void stopReview() {
    	ReviewSummary summary = service.getReviewSummary(); 
    	
    	String message= "Review Finished!\n\n" + "Correct: " + summary.getCorrect() + "\n" + "Incorrect: " + summary.getIncorrect() + "\n" + "Total answered: " + summary.getTotal(); 
    	
    	JOptionPane.showMessageDialog(frame,
    			message, "Review Results", JOptionPane.INFORMATION_MESSAGE);
    	
    	new Homepage(service); 
    	frame.dispose();
    }
   
    // ==========================================
    // REFRESH GUI
    // ==========================================

    private void panelRefresh() {

        frame.revalidate();
        frame.repaint();
    }
}