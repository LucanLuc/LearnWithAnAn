
package frontend;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import Backend.VocabularyService;
import model.Word;
import model.AIGeneratedWordContent;

public class addWordPage {

    private final VocabularyService service;

    public addWordPage(VocabularyService service) {

        this.service = service;

        JFrame frame = new JFrame("Add Word");
        Style.stylePage(frame);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 35, 20, 35)
        );

        // =========================
        // TITLE
        // =========================

        JLabel title = new JLabel("Vocabulary");
        Style.styleHeading1(title);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // =========================
        // WORD INPUT
        // =========================

        JLabel wordLabel = new JLabel("Enter an English word:");

        JTextField vocabTextField = new JTextField(30);

        // =========================
        // STATUS
        // =========================

        JLabel statusLabel = new JLabel(
                "Type a word to generate learning content automatically."
        );

        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel imageStatusLabel = new JLabel("Image: Waiting");

        // =========================
        // TEXT PREVIEW
        // =========================

        JLabel translationLabel =
                new JLabel("Vietnamese Translation:");

        JTextArea translationArea = new JTextArea(2, 30);
        translationArea.setLineWrap(true);
        translationArea.setWrapStyleWord(true);
        translationArea.setEditable(false);

        JLabel explanationLabel =
                new JLabel("Simple English Explanation:");

        JTextArea explanationArea = new JTextArea(3, 30);
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setEditable(false);

        JLabel exampleLabel =
                new JLabel("Example Sentence:");

        JTextArea exampleArea = new JTextArea(3, 30);
        exampleArea.setLineWrap(true);
        exampleArea.setWrapStyleWord(true);
        exampleArea.setEditable(false);

        // =========================
        // IMAGE PREVIEW
        // =========================
        JLabel imageLabel = new JLabel(
                "Your vocabulary illustration will appear here.",
                SwingConstants.CENTER
        );

        JProgressBar imageProgress = new JProgressBar();
        imageProgress.setIndeterminate(true);
        imageProgress.setVisible(false);

        JPanel imageLoadingPanel = new JPanel();
        imageLoadingPanel.setLayout(
                new BoxLayout(imageLoadingPanel, BoxLayout.Y_AXIS)
        );

        JLabel loadingText = new JLabel("Creating your illustration...");
        loadingText.setAlignmentX(Component.CENTER_ALIGNMENT);

        imageProgress.setAlignmentX(Component.CENTER_ALIGNMENT);

        imageLoadingPanel.add(loadingText);
        imageLoadingPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        imageLoadingPanel.add(imageProgress);
        imageLoadingPanel.setVisible(false);

        imageLabel.setPreferredSize(new Dimension(300, 220));
        imageLabel.setMinimumSize(new Dimension(300, 220));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(
                new BoxLayout(imagePanel, BoxLayout.Y_AXIS)
        );

        imagePanel.setBorder(
                BorderFactory.createTitledBorder("AI Image Preview")
        );

        imagePanel.add(imageLabel);
        imagePanel.add(imageLoadingPanel);
        imagePanel.add(imageStatusLabel);

        // =========================
        // PREVIEW PANEL
        // =========================

        JPanel previewPanel = new JPanel();
        previewPanel.setLayout(
                new BoxLayout(previewPanel, BoxLayout.Y_AXIS)
        );

        previewPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "AI-Generated Content Preview"
                )
        );

        previewPanel.add(translationLabel);
        previewPanel.add(new JScrollPane(translationArea));

        previewPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        previewPanel.add(explanationLabel);
        previewPanel.add(new JScrollPane(explanationArea));

        previewPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        previewPanel.add(exampleLabel);
        previewPanel.add(new JScrollPane(exampleArea));

        previewPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        previewPanel.add(imagePanel);

        // =========================
        // BUTTONS
        // =========================

        JButton saveBtn = new JButton("Save Word");
        saveBtn.setEnabled(false);

        JButton deleteWordBtn = new JButton("Delete Word");

        JButton returnBtn = new JButton("Return");

        JPanel controlPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 12, 5)
        );

        controlPanel.add(saveBtn);
        controlPanel.add(deleteWordBtn);
        controlPanel.add(returnBtn);

        // =========================
        // GENERATION STATE
        // =========================

        final AIGeneratedWordContent[] generatedContent = {null};

        final String[] generatedImagePath = {null};

        // Changes whenever the input word changes
        final int[] generationId = {0};

        // Tracks words that have already been saved
        Set<String> savedWords = new HashSet<>();

        final boolean[] textReady = {false};

        // =========================
        // DEBOUNCE TIMER
        // =========================

        Timer debounceTimer = new Timer(1000, e -> {

            String word = vocabTextField.getText().trim();

            if (word.isEmpty()) {
                return;
            }

            if (service.searchWord(word) != null) {

                statusLabel.setText(
                        "This word already exists in your dictionary."
                );

                return;
            }

            final int currentId = generationId[0];

            statusLabel.setText("Generating vocabulary content...");

            // =========================
            // TEXT GENERATION
            // =========================

            SwingWorker<AIGeneratedWordContent, Void> textWorker =
                    new SwingWorker<>() {

                @Override
                protected AIGeneratedWordContent doInBackground()
                        throws Exception {

                    return service.generateWordDetails(word);
                }

                @Override
                protected void done() {

                    // Ignore outdated results
                    if (currentId != generationId[0]) {
                        return;
                    }

                    try {

                        AIGeneratedWordContent content = get();

                        generatedContent[0] = content;

                        translationArea.setText(
                                content.getTranslation()
                        );

                        explanationArea.setText(
                                content.getSimpleExplanation()
                        );

                        exampleArea.setText(
                                content.getExampleSentence()
                        );

                        textReady[0] = true;

                        saveBtn.setEnabled(true);

                        statusLabel.setText(
                                "Text generated successfully. Generating image..."
                        		
             
                        );
                        imageLabel.setIcon(null);
                        imageLabel.setText("Preparing your illustration...");

                        imageLoadingPanel.setVisible(true);
                        imageProgress.setVisible(true);

                        imageStatusLabel.setText("Image generation in progress...");

                        // =========================
                        // IMAGE GENERATION
                        // =========================

                        SwingWorker<String, Void> imageWorker =
                                new SwingWorker<>() {

                            @Override
                            protected String doInBackground()
                                    throws Exception {

                                return service.generateWordImage(
                                        word,
                                        content
                                );
                            }

                            @Override
                            protected void done() {

                                String imagePath = null;

                                try {

                                    imagePath = get();

                                    generatedImagePath[0] = imagePath;
                                    
                                    imageLoadingPanel.setVisible(false);
                                    imageProgress.setVisible(false);

                                    ImageIcon originalIcon =
                                            new ImageIcon(imagePath);

                                    Image scaledImage =
                                            originalIcon.getImage()
                                                    .getScaledInstance(
                                                            300,
                                                            220,
                                                            Image.SCALE_SMOOTH
                                                    );

                                    imageLabel.setText("");
                                    imageLabel.setIcon(
                                            new ImageIcon(scaledImage)
                                    );

                                    imageStatusLabel.setText(
                                            "Image generated successfully."
                                    );

                                    // If the word was already saved,
                                    // attach the image to its database entry.
                                    if (savedWords.contains(
                                            word.toLowerCase()
                                    )) {

                                        service.updateGeneratedImage(
                                                word,
                                                imagePath
                                        );
                                    }

                                } catch (Exception ex) {

                                    imageStatusLabel.setText(
                                            "Image generation failed."
                                    );

                                    System.err.println(
                                            "Image generation error: "
                                                    + ex.getMessage()
                                    );
                                    
                                    imageLoadingPanel.setVisible(false);
                                    imageProgress.setVisible(false);
                                }

                                // Only update current UI for the
                                // word that is still being displayed.
                                if (currentId == generationId[0]) {

                                    if (imagePath != null) {
                                        statusLabel.setText(
                                                "All content generated successfully."
                                        );
                                    } else {
                                        statusLabel.setText(
                                                "Text ready. Image could not be generated."
                                        );
                                    }
                                }
                            }
                        };

                        imageWorker.execute();

                    } catch (Exception ex) {

                        Throwable cause = ex.getCause();

                        String message = cause != null
                                ? cause.getMessage()
                                : ex.getMessage();

                        statusLabel.setText(
                                "Text generation failed."
                        );

                        JOptionPane.showMessageDialog(
                                frame,
                                "AI generation failed:\n" + message,
                                "Generation Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            };

            textWorker.execute();

        });

        debounceTimer.setRepeats(false);

        // =========================
        // INPUT LISTENER
        // =========================

        vocabTextField.getDocument().addDocumentListener(
                new DocumentListener() {

            private void inputChanged() {
            	
            	imageLoadingPanel.setVisible(false);
            	imageProgress.setVisible(false);

                generationId[0]++;

                debounceTimer.stop();

                generatedContent[0] = null;
                generatedImagePath[0] = null;

                textReady[0] = false;

                translationArea.setText("");
                explanationArea.setText("");
                exampleArea.setText("");

                imageLabel.setIcon(null);
                imageLabel.setText(
                        "Your vocabulary illustration will appear here."
                );

                imageStatusLabel.setText("Image: Waiting");

                saveBtn.setEnabled(false);

                String word = vocabTextField.getText().trim();

                if (word.isEmpty()) {

                    statusLabel.setText(
                            "Type a word to generate learning content automatically."
                    );

                    return;
                }

                if (word.length() < 2) {

                    statusLabel.setText(
                            "Keep typing..."
                    );

                    return;
                }

                statusLabel.setText(
                        "Waiting for you to finish typing..."
                );

                debounceTimer.start();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                inputChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                inputChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                inputChanged();
            }
        });

        // =========================
        // SAVE WORD
        // =========================

        saveBtn.addActionListener(e -> {

            String word = vocabTextField.getText().trim();

            if (!textReady[0] || generatedContent[0] == null) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please wait for text generation to finish."
                );

                return;
            }

            if (service.searchWord(word) != null) {

                JOptionPane.showMessageDialog(
                        frame,
                        "This word already exists in your dictionary."
                );

                return;
            }

            try {

                // Save without starting another image request.
                service.saveGeneratedWord(
                        word,
                        generatedContent[0],
                        generatedImagePath[0],
                        false
                );

                savedWords.add(word.toLowerCase());

                JOptionPane.showMessageDialog(
                        frame,
                        "Word added successfully!"
                );

                // Clear the input and reset the preview
                vocabTextField.setText("");

                frame.revalidate();
                frame.repaint();

                vocabTextField.requestFocusInWindow();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Could not save word:\n" + ex.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
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

                int confirmation = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to delete "
                                + wordInput + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmation == JOptionPane.YES_OPTION) {

                    service.deleteWord(wordToDelete);

                    JOptionPane.showMessageDialog(
                            frame,
                            "Word deleted successfully."
                    );

                    vocabTextField.setText("");
                }

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

            debounceTimer.stop();

            new Homepage(service);

            frame.dispose();
        });

        // =========================
        // ADD COMPONENTS
        // =========================

        panel.add(title);

        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        panel.add(wordLabel);
        panel.add(vocabTextField);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(statusLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(previewPanel);

        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(controlPanel);

        frame.add(new JScrollPane(panel));

        frame.setSize(650, 850);

        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.setVisible(true);

        vocabTextField.requestFocusInWindow();
    }
}
