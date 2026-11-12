package frontend;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Backend.VocabularyService;
import model.Word;

public class deleteWord {

    private VocabularyService service;
    private Word selectedWord; 

    public deleteWord(VocabularyService service) {

        this.service = service;

        JFrame frame = new JFrame("Manage Words");
        Style.stylePage(frame);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Manage Words");
        Style.styleHeading1(title);

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(title);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel wordPanel = new JPanel();
        wordPanel.setLayout(new BoxLayout(wordPanel, BoxLayout.Y_AXIS));
        
        JTextField searchField = new JTextField(); 
		JButton searchBtn = new JButton("Search"); 
		Style.styleButton(searchBtn); 
		
		JLabel searchResult = new JLabel("");
		
		searchBtn.addActionListener(e -> {
			String word = searchField.getText().trim(); 
			selectedWord = service.searchWord(word); 
			
			if (selectedWord != null) {
				searchResult.setText(selectedWord.getWord() + " : " + selectedWord.getDefinition()); 
			} else {
				searchResult.setText("Word not found."); 
			}
		});
		mainPanel.add(searchField); 
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        mainPanel.add(searchBtn); 
        mainPanel.add(Box.createRigidArea(new Dimension(0,10))); 
        mainPanel.add(searchResult); 
		
		

		for (Word word : service.getAllWords()) {
		
		    JPanel row = new JPanel(new BorderLayout(10, 0));
		
		    JTextField wordField = new JTextField(word.getWord());
		    JTextField definitionField = new JTextField(word.getDefinition());
		
		    wordField.setPreferredSize(new Dimension(130, 30));
		    definitionField.setPreferredSize(new Dimension(250, 30));
		
		    // 1. Display the vocabulary image
		    JLabel imageLabel = new JLabel("No image");
		    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		    imageLabel.setPreferredSize(new Dimension(100, 100));
		
		    String imagePath = word.getImagePath();
		
		    if (imagePath != null && !imagePath.isBlank()) {
		
		        ImageIcon originalIcon = new ImageIcon(imagePath);
		
		        if (originalIcon.getIconWidth() > 0) {
		
		            Image scaledImage = originalIcon.getImage()
		                .getScaledInstance(
		                    90,
		                    90,
		                    Image.SCALE_SMOOTH
		                );
		
		            imageLabel.setIcon(new ImageIcon(scaledImage));
		            imageLabel.setText("");
		
		        }
		    }
		
		    // 2. Display the date
		    JLabel dateLabel = new JLabel(
		        word.getDateAdded().toLocalDate().toString()
		    );
		
		    // 3. Save changes
		    JButton saveButton = new JButton("Save");
		
		    saveButton.addActionListener(e -> {
		
		        String newWord = wordField.getText().trim();
		        String newDefinition = definitionField.getText().trim();
		
		        if (newWord.isEmpty() || newDefinition.isEmpty()) {
		            JOptionPane.showMessageDialog(
		                frame,
		                "Word and definition cannot be empty."
		            );
		            return;
		        }
		
		        service.updateWord(word, newWord, newDefinition);
		
		        JOptionPane.showMessageDialog(
		            frame,
		            "Word updated successfully."
		        );
		    });
		
		    // 4. Delete word
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
		
		    // 5. Arrange the buttons
		    JPanel buttonPanel = new JPanel();
		
		    buttonPanel.add(saveButton);
		    buttonPanel.add(deleteButton);
		
		    // 6. Arrange date and buttons vertically
		    JPanel informationPanel = new JPanel();
		
		    informationPanel.setLayout(
		        new BoxLayout(informationPanel, BoxLayout.Y_AXIS)
		    );
		
		    informationPanel.add(dateLabel);
		    informationPanel.add(buttonPanel);
		
		    // 7. Assemble the vocabulary row
		    row.add(imageLabel, BorderLayout.WEST);
		    row.add(wordField, BorderLayout.CENTER);
		
		    JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
		
		    rightPanel.add(definitionField, BorderLayout.NORTH);
		    rightPanel.add(informationPanel, BorderLayout.CENTER);
		
		    row.add(rightPanel, BorderLayout.CENTER);
		
		    wordPanel.add(row);
		    wordPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		}

      
        
        
        JScrollPane scrollPane = new JScrollPane(wordPanel);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        mainPanel.add(scrollPane);
        mainPanel.add(
                Box.createRigidArea(new Dimension(0, 30))
        );

        JButton returnButton = new JButton("Return");

        returnButton.addActionListener(e -> {
            new Homepage();
            frame.dispose();
        });
    
        
        mainPanel.add(returnButton);
        mainPanel.add(Box.createVerticalGlue());

        frame.add(mainPanel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}