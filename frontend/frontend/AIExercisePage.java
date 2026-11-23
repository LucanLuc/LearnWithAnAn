
package frontend;

import Backend.AIExerciseTracker;
import Backend.VocabularyService;
import Backend.WordLearningStats;

import ai.AIExerciseParser;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

import javax.swing.*;

import model.AIExercise;
import model.AIExerciseResult;
import model.Word;

public class AIExercisePage {

    private final VocabularyService service;
    private final Word word;

    private JFrame frame;
    private JLabel statusLabel;
    private JLabel difficultyLabel;
    private JTextArea questionArea;
    private JButton[] optionButtons;
    private JButton submitButton;

    private AIExercise exercise;
    private String selectedAnswer;

    public AIExercisePage(
            VocabularyService service,
            Word word) {

        this.service = service;
        this.word = word;

        initializeUI();
        generateExercise();
    }

    private void initializeUI() {

        frame = new JFrame("AI Vocabulary Exercise");
        frame.setSize(750, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        );

        JLabel heading = new JLabel("AI Personalized Exercise");
        heading.setFont(new Font("Arial", Font.BOLD, 28));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel wordLabel = new JLabel(
                "Target word: " + word.getWord()
        );
        wordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        wordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        difficultyLabel = new JLabel("Difficulty: Generating...");
        difficultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        questionArea = new JTextArea(4, 40);
        questionArea.setFont(new Font("Arial", Font.PLAIN, 17));
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setEditable(false);
        questionArea.setOpaque(false);

        optionButtons = new JButton[4];

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(4, 1, 10, 10));

        for (int i = 0; i < 4; i++) {

            optionButtons[i] = new JButton();
            optionButtons[i].setFont(
                    new Font("Arial", Font.PLAIN, 15)
            );

            final int index = i;

            optionButtons[i].addActionListener(e -> {
                selectedAnswer = String.valueOf(
                        (char) ('A' + index)
                );

                updateSelectedOption();
            });

            optionButtons[i].setEnabled(false);
            optionsPanel.add(optionButtons[i]);
        }

        submitButton = new JButton("Submit Answer");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.setEnabled(false);
        submitButton.addActionListener(e -> submitAnswer());

        statusLabel = new JLabel("Generating your exercise...");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> frame.dispose());

        main.add(heading);
        main.add(Box.createVerticalStrut(15));
        main.add(wordLabel);
        main.add(Box.createVerticalStrut(10));
        main.add(difficultyLabel);
        main.add(Box.createVerticalStrut(25));
        main.add(questionArea);
        main.add(Box.createVerticalStrut(20));
        main.add(optionsPanel);
        main.add(Box.createVerticalStrut(20));
        main.add(submitButton);
        main.add(Box.createVerticalStrut(15));
        main.add(statusLabel);
        main.add(Box.createVerticalStrut(20));
        main.add(closeButton);

        frame.add(main);
        frame.setVisible(true);
    }

    private void generateExercise() {

        statusLabel.setText("Generating personalized exercise...");

        CompletableFuture.supplyAsync(() -> {

            try {

                WordLearningStats stats =
                        service.getLearningStats().get(word);

                String response =
                        service.generatePersonalizedExercise(
                                word,
                                stats
                        );

                return new AIExerciseParser().parse(response);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }).whenComplete((generated, error) -> {

            SwingUtilities.invokeLater(() -> {

                if (error != null) {

                    statusLabel.setText(
                            "Could not generate exercise."
                    );

                    JOptionPane.showMessageDialog(
                            frame,
                            error.getMessage(),
                            "AI Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    return;
                }

                exercise = generated;

                questionArea.setText(exercise.getQuestion());

                difficultyLabel.setText(
                        "Difficulty: " + exercise.getDifficulty()
                );

                String[] options = exercise.getOptions();

                for (int i = 0; i < 4; i++) {

                    optionButtons[i].setText(
                            (char) ('A' + i) + ". " + options[i]
                    );

                    optionButtons[i].setEnabled(true);
                }

                submitButton.setEnabled(true);
                statusLabel.setText("Choose your answer.");
            });
        });
    }

    private void updateSelectedOption() {

        for (int i = 0; i < 4; i++) {

            if (selectedAnswer != null &&
                    selectedAnswer.equals(
                            String.valueOf((char) ('A' + i)))) {

                optionButtons[i].setBackground(
                        new Color(190, 220, 255)
                );

            } else {

                optionButtons[i].setBackground(
                        UIManager.getColor("Button.background")
                );
            }
        }
    }

    private void submitAnswer() {

        if (exercise == null || selectedAnswer == null) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Please select an answer first."
            );

            return;
        }

        boolean correct = exercise.checkAnswer(selectedAnswer);

        AIExerciseResult result = new AIExerciseResult(
                word.getWord(),
                exercise.getDifficulty(),
                correct,
                selectedAnswer,
                exercise.getCorrectAnswer()
        );

        service.recordAIExerciseResult(result);

        String feedback = correct
                ? "Correct! Well done."
                : "Not quite. The correct answer is "
                        + exercise.getCorrectAnswer() + ".";

        statusLabel.setText(feedback);

        String explanation = exercise.getExplanation();

        JOptionPane.showMessageDialog(
                frame,
                feedback + "\n\n" + explanation,
                "Exercise Result",
                correct
                        ? JOptionPane.INFORMATION_MESSAGE
                        : JOptionPane.WARNING_MESSAGE
        );

        submitButton.setEnabled(false);

        for (JButton button : optionButtons) {
            button.setEnabled(false);
        }
    }
}
