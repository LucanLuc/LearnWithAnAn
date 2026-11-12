
package frontend;

import java.awt.*;
import javax.swing.*;

import Backend.VocabularyService;
import model.Word;

public class addWordPage {

    private VocabularyService service;

    public addWordPage(VocabularyService service) {

        this.service = service;

        JFrame frame = new JFrame("Add Word");
        Style.stylePage(frame);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // =========================
        // TITLE
        // =========================

        JLabel title = new JLabel("Vocabulary");
        Style.styleHeading1(title);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // =========================
        // WORD INPUT
        // =========================

        JLabel H1 = new JLabel("Enter the word:");

        JTextField vocabTextField = new JTextField(30);

        // =========================
        // DEFINITION INPUT
        // =========================

        JLabel H2 = new JLabel("Enter the definition:");

        JTextField defTextField = new JTextField(30);

        // =========================
        // BUTTONS
        // =========================

        JButton submitBtn = new JButton("Submit");

        JButton deleteWordBtn = new JButton("Delete Word");

        JButton returnBtn = new JButton("Return");

        // =========================
        // SUBMIT FUNCTION
        // =========================

        Runnable submitWord = () -> {

            String word = vocabTextField.getText().trim();
            String definition = defTextField.getText().trim();

            if (word.isEmpty() || definition.isEmpty()) {

                JOptionPane.showMessageDialog(
                    frame,
                    "Please enter both the word and definition."
                );

                return;
            }

            service.addWord(word, definition);

            JOptionPane.showMessageDialog(
                frame,
                "Word added successfully!"
            );

            vocabTextField.setText("");
            defTextField.setText("");

            vocabTextField.requestFocusInWindow();
        };

        submitBtn.addActionListener(e -> submitWord.run());

        // =========================
        // ENTER KEY NAVIGATION
        // =========================

        vocabTextField.addActionListener(e -> {
            defTextField.requestFocusInWindow();
        });

        defTextField.addActionListener(e -> {
            submitWord.run();
        });

        // =========================
        // DELETE WORD
        // =========================

        deleteWordBtn.addActionListener(e -> {

            String wordInput = vocabTextField.getText().trim();

            if (wordInput.isEmpty()) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please enter a word to delete."
                );
                return;
            }

            Word wordToDelete = service.searchWord(wordInput);

            if (wordToDelete != null) {

                service.deleteWord(wordToDelete);

                JOptionPane.showMessageDialog(
                    frame,
                    "Word deleted successfully."
                );

                vocabTextField.setText("");
                defTextField.setText("");

            } else {

                JOptionPane.showMessageDialog(
                    frame,
                    "Word not found."
                );
            }
        });

        // =========================
        // RETURN
        // =========================

        returnBtn.addActionListener(e -> {

            new Homepage(service);

            frame.dispose();
        });

        // =========================
        // CONTROL PANEL
        // =========================

        JPanel controlPanel = new JPanel();

        controlPanel.setLayout(
            new FlowLayout(FlowLayout.CENTER, 20, 0)
        );

        controlPanel.add(submitBtn);
        controlPanel.add(deleteWordBtn);
        controlPanel.add(returnBtn);

        // =========================
        // ADD COMPONENTS
        // =========================

        panel.add(Box.createVerticalGlue());

        panel.add(title);

        panel.add(Box.createRigidArea(new Dimension(0, 40)));

        panel.add(H1);
        panel.add(vocabTextField);

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        panel.add(H2);
        panel.add(defTextField);

        panel.add(Box.createRigidArea(new Dimension(0, 40)));

        panel.add(controlPanel);

        panel.add(Box.createVerticalGlue());

        // =========================
        // FRAME
        // =========================

        frame.add(panel);

        frame.setSize(500, 500);

        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.setVisible(true);

        vocabTextField.requestFocusInWindow();
    }
}
