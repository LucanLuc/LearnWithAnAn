
package frontend;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Backend.VocabularyService;
import model.Word;

public class deleteWord {

    private final VocabularyService service;
    private Word selectedWord;

    public deleteWord(VocabularyService service) {

        this.service = service;

        JFrame frame = new JFrame("Manage Words");
        Style.stylePage(frame);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(
                new BoxLayout(mainPanel, BoxLayout.Y_AXIS)
        );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        // =========================
        // TITLE
        // =========================

        JLabel title = new JLabel("Manage Words");
        Style.styleHeading1(title);
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        mainPanel.add(title);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // =========================
        // SEARCH
        // =========================

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 5)
        );

        JTextField searchField = new JTextField(25);

        JButton searchBtn = new JButton("Search");
        Style.styleButton(searchBtn);

        JLabel searchResult = new JLabel(" ");

        searchBtn.addActionListener(e -> {

            String word = searchField.getText().trim();

            if (word.isEmpty()) {
                searchResult.setText("Please enter a word.");
                selectedWord = null;
                return;
            }

            selectedWord = service.searchWord(word);

            if (selectedWord != null) {

                searchResult.setText(
                        selectedWord.getWord()
                        + " : "
                        + selectedWord.getDefinition()
                );

            } else {

                searchResult.setText("Word not found.");
            }
        });

        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        mainPanel.add(searchPanel);
        mainPanel.add(searchResult);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // =========================
        // WORD LIST
        // =========================

        JPanel wordPanel = new JPanel();

        wordPanel.setLayout(
                new BoxLayout(wordPanel, BoxLayout.Y_AXIS)
        );

        for (Word word : service.getAllWords()) {

            JPanel row = new JPanel(new BorderLayout(15, 5));

            row.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    java.awt.Color.LIGHT_GRAY
                            ),
                            BorderFactory.createEmptyBorder(
                                    12, 12, 12, 12
                            )
                    )
            );

            // =========================
            // IMAGE
            // =========================

            JLabel imageLabel = new JLabel("No image");

            imageLabel.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            imageLabel.setPreferredSize(
                    new Dimension(100, 110)
            );

            String imagePath = word.getImagePath();

            if (imagePath != null && !imagePath.isBlank()) {

                ImageIcon originalIcon = new ImageIcon(imagePath);

                if (originalIcon.getIconWidth() > 0) {

                    Image scaledImage = originalIcon.getImage()
                            .getScaledInstance(
                                    100,
                                    100,
                                    Image.SCALE_SMOOTH
                            );

                    imageLabel.setIcon(
                            new ImageIcon(scaledImage)
                    );

                    imageLabel.setText("");
                }
            }

            // =========================
            // WORD AND DEFINITION
            // =========================

            JPanel detailsPanel = new JPanel();

            detailsPanel.setLayout(
                    new BoxLayout(detailsPanel, BoxLayout.Y_AXIS)
            );

            JTextField wordField = new JTextField(
                    word.getWord()
            );

            wordField.setFont(
                    wordField.getFont().deriveFont(
                            java.awt.Font.BOLD,
                            18f
                    )
            );

            JTextField definitionField = new JTextField(
                    word.getDefinition()
            );

            definitionField.setBorder(
                    BorderFactory.createTitledBorder(
                            "Definition"
                    )
            );

            detailsPanel.add(wordField);
            detailsPanel.add(Box.createRigidArea(
                    new Dimension(0, 8)
            ));

            detailsPanel.add(definitionField);

            // =========================
            // VIETNAMESE TRANSLATION
            // =========================

            JTextArea translationArea = createReadOnlyArea(
                    word.getTranslation()
            );

            detailsPanel.add(createContentPanel(
                    "Vietnamese Translation",
                    translationArea
            ));

            // =========================
            // SIMPLE EXPLANATION
            // =========================

            JTextArea explanationArea = createReadOnlyArea(
                    word.getSimpleExplanation()
            );

            detailsPanel.add(createContentPanel(
                    "Simple English Explanation",
                    explanationArea
            ));

            // =========================
            // EXAMPLE SENTENCE
            // =========================

            JTextArea exampleArea = createReadOnlyArea(
                    word.getExampleSentence()
            );

            detailsPanel.add(createContentPanel(
                    "Example Sentence",
                    exampleArea
            ));

            // =========================
            // DATE
            // =========================

            JLabel dateLabel = new JLabel(
                    "Added: "
                    + word.getDateAdded().toLocalDate()
            );

            detailsPanel.add(Box.createRigidArea(
                    new Dimension(0, 8)
            ));

            detailsPanel.add(dateLabel);

            // =========================
            // SAVE BUTTON
            // =========================

            JButton saveButton = new JButton("Save");

            saveButton.addActionListener(e -> {

                String newWord = wordField.getText().trim();

                String newDefinition =
                        definitionField.getText().trim();

                if (newWord.isEmpty()
                        || newDefinition.isEmpty()) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Word and definition cannot be empty."
                    );

                    return;
                }

                try {

                    service.updateWord(
                            word,
                            newWord,
                            newDefinition
                    );

                    JOptionPane.showMessageDialog(
                            frame,
                            "Word updated successfully."
                    );

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Could not update word:\n"
                                    + ex.getMessage(),
                            "Update Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });

            // =========================
            // DELETE BUTTON
            // =========================

            JButton deleteButton = new JButton("Delete");

            deleteButton.addActionListener(e -> {

                int choice = JOptionPane.showConfirmDialog(
                        frame,
                        "Delete " + word.getWord() + "?",
                        "Confirm deletion",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {

                    service.deleteWord(word);

                    wordPanel.remove(row);

                    wordPanel.revalidate();
                    wordPanel.repaint();
                }
            });

            // =========================
            // BUTTON PANEL
            // =========================

            JPanel buttonPanel = new JPanel();

            buttonPanel.setLayout(
                    new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)
            );

            buttonPanel.add(saveButton);

            buttonPanel.add(Box.createRigidArea(
                    new Dimension(0, 10)
            ));

            buttonPanel.add(deleteButton);

            // =========================
            // ASSEMBLE ROW
            // =========================

            row.add(imageLabel, BorderLayout.WEST);

            // IMPORTANT:
            // Details and buttons now have separate positions.
            // Neither component replaces the other.

            row.add(detailsPanel, BorderLayout.CENTER);

            row.add(buttonPanel, BorderLayout.EAST);

            wordPanel.add(row);

            wordPanel.add(Box.createRigidArea(
                    new Dimension(0, 12)
            ));
        }

        // =========================
        // SCROLL PANE
        // =========================

        JScrollPane scrollPane = new JScrollPane(wordPanel);

        scrollPane.setPreferredSize(
                new Dimension(850, 500)
        );

        // Increase vertical scrolling speed.

        scrollPane.getVerticalScrollBar().setUnitIncrement(35);

        scrollPane.getVerticalScrollBar().setBlockIncrement(250);

        scrollPane.setWheelScrollingEnabled(true);

        mainPanel.add(scrollPane);

        mainPanel.add(Box.createRigidArea(
                new Dimension(0, 20)
        ));

        // =========================
        // RETURN BUTTON
        // =========================

        JButton returnButton = new JButton("Return");

        returnButton.addActionListener(e -> {

            new Homepage();
            frame.dispose();
        });

        JPanel returnPanel = new JPanel();

        returnPanel.add(returnButton);

        mainPanel.add(returnPanel);

        // =========================
        // FRAME
        // =========================

        frame.add(mainPanel);

        frame.setSize(950, 800);

        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        frame.setVisible(true);
    }

    // =========================
    // HELPER: CREATE READ-ONLY TEXT AREA
    // =========================

    private JTextArea createReadOnlyArea(String content) {

        JTextArea area = new JTextArea(
                content == null || content.isBlank()
                        ? "Not available"
                        : content
        );

        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);

        area.setOpaque(false);

        return area;
    }

    // =========================
    // HELPER: CREATE CONTENT PANEL
    // =========================

    private JPanel createContentPanel(
            String title,
            JTextArea area
    ) {

        JPanel panel = new JPanel(new BorderLayout());

        panel.setBorder(
                BorderFactory.createTitledBorder(title)
        );

        panel.add(area, BorderLayout.CENTER);

        return panel;
    }
}
