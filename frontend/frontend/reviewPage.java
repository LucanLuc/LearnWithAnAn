
package frontend;

import java.awt.*;
import java.util.List;
import javax.swing.*;

import Backend.VocabularyService;
import model.ReviewResult;
import model.ReviewSummary;
import model.Word;

public class reviewPage {

    private JFrame frame;

    private JLabel wordLabel;
    private JLabel imageLabel;
    private JLabel scoreLabel;
    private JLabel resultLabel;

    private JButton[] answerButtons;
    private JButton stopButton;

    private Word currentWord;
    private VocabularyService service;

    private Timer imageRefreshTimer;
    private String displayedImagePath = "";

    public reviewPage(VocabularyService service) {

        this.service = service;

        service.startReview();

        // FRAME
        frame = new JFrame("Review");
        Style.stylePage(frame);

        JPanel panel = new JPanel();
        panel.setLayout(
            new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        // TITLE
        JLabel title = new JLabel("Review");
        Style.styleHeading1(title);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // SCORE
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // WORD
        wordLabel = new JLabel("Word");
        wordLabel.setFont(
            new Font("Arial", Font.BOLD, 30)
        );
        wordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // IMAGE
        imageLabel = new JLabel("Image will appear here");
        imageLabel.setPreferredSize(new Dimension(180, 180));
        imageLabel.setMinimumSize(new Dimension(180, 180));
        imageLabel.setMaximumSize(new Dimension(180, 180));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // RESULT
        resultLabel = new JLabel(" ");
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ANSWER BUTTONS
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

        // STOP BUTTON
        stopButton = new JButton("Stop Review");

        stopButton.addActionListener(e -> {
            stopReview();
        });

        // ADD COMPONENTS
        panel.add(Box.createVerticalGlue());

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(scoreLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        panel.add(wordLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(imageLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(answerPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(resultLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(stopButton);

        panel.add(Box.createVerticalGlue());

        // SHOW FRAME
        frame.add(panel);
        frame.setSize(500, 750);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Check whether background image generation has finished.
        imageRefreshTimer = new Timer(1000, e -> {
            refreshWordImage();
        });

        imageRefreshTimer.start();

        loadNextQuestion();
    }

    // LOAD NEXT QUESTION
    private void loadNextQuestion() {

        currentWord = service.getNextReviewWord();

        displayedImagePath = "";

        if (currentWord == null) {

            wordLabel.setText("No words available");

            imageLabel.setIcon(null);
            imageLabel.setText("");

            for (JButton button : answerButtons) {
                button.setEnabled(false);
            }

            panelRefresh();
            return;
        }

        wordLabel.setText(currentWord.getWord());

        // Display image if it has already been generated.
        refreshWordImage();

        List<String> choices = service.getReviewChoices();

        for (int i = 0; i < answerButtons.length; i++) {

            if (i < choices.size()) {

                answerButtons[i].setText(choices.get(i));
                answerButtons[i].setEnabled(true);
                answerButtons[i].setVisible(true);

            } else {

                answerButtons[i].setVisible(false);
            }
        }

        resultLabel.setText(" ");

        panelRefresh();
    }

    // REFRESH WORD IMAGE
    private void refreshWordImage() {

        if (currentWord == null) {
            return;
        }

        String imagePath = currentWord.getImagePath();

        // Image has not been generated yet.
        if (imagePath == null || imagePath.isBlank()) {

            if (!displayedImagePath.isEmpty()) {
                displayedImagePath = "";
            }

            imageLabel.setIcon(null);
            imageLabel.setText("Image is being generated...");

            return;
        }

        // Avoid loading the same image repeatedly.
        if (imagePath.equals(displayedImagePath)) {
            return;
        }

        ImageIcon originalImage = new ImageIcon(imagePath);

        // Image file may not be ready yet.
        if (originalImage.getIconWidth() <= 0) {
            return;
        }

        Image scaledImage = originalImage.getImage().getScaledInstance(
            180,
            180,
            Image.SCALE_SMOOTH
        );

        imageLabel.setText("");
        imageLabel.setIcon(new ImageIcon(scaledImage));

        displayedImagePath = imagePath;

        panelRefresh();
    }

    // CHECK ANSWER
    private void checkAnswer(int selectedIndex) {

        String selectedAnswer =
            answerButtons[selectedIndex].getText();

        ReviewResult result =
            service.answerReview(selectedAnswer);

        // Disable all answer buttons.
        for (JButton button : answerButtons) {
            button.setEnabled(false);
        }

        // Display result.
        if (result.isCorrect()) {

            resultLabel.setText("Correct! ✓");

        } else {

            resultLabel.setText(
                "Incorrect. Correct answer: "
                + result.getWord().getDefinition()
            );
        }

        // Update score.
        scoreLabel.setText(
            "Score: "
            + service.getReviewCorrect()
            + " / "
            + service.getReviewTotal()
        );

        panelRefresh();

        // Load the next question after one second.
        Timer timer = new Timer(1000, e -> {
            loadNextQuestion();
        });

        timer.setRepeats(false);
        timer.start();
    }

    // STOP REVIEW
    private void stopReview() {

        imageRefreshTimer.stop();

        ReviewSummary summary = service.getReviewSummary();

        String message =
            "Review Finished!\n\n"
            + "Correct: " + summary.getCorrect() + "\n"
            + "Incorrect: " + summary.getIncorrect() + "\n"
            + "Total answered: " + summary.getTotal();

        JOptionPane.showMessageDialog(
            frame,
            message,
            "Review Results",
            JOptionPane.INFORMATION_MESSAGE
        );

        new Homepage(service);
        frame.dispose();
    }

    // REFRESH GUI
    private void panelRefresh() {
        frame.revalidate();
        frame.repaint();
    }
}
