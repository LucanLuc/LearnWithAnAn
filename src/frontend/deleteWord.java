package frontend;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import Backend.VocabularyService;
import Backend.Word;

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
            
            JLabel dateLabel = new JLabel(
                word.getDateAdded().toLocalDate().toString()
            );

            wordField.setPreferredSize(new Dimension(150, 30));
            definitionField.setPreferredSize(new Dimension(300, 30));
            dateLabel.setPreferredSize(new Dimension(100, 30));

            JButton saveButton = new JButton("Save");

            saveButton.addActionListener(e -> {

                String newWord = wordField.getText();
                String newDefinition = definitionField.getText();

                service.updateWord(
                    word,
                    newWord,
                    newDefinition
                );
            });

            JButton deleteButton = new JButton("Delete");

            deleteButton.addActionListener(e -> {

                service.deleteWord(word);

                wordPanel.remove(row);
                wordPanel.revalidate();
                wordPanel.repaint();
            });

            JPanel buttonPanel = new JPanel();

            buttonPanel.add(saveButton);
            buttonPanel.add(deleteButton);

            row.add(wordField, BorderLayout.WEST);
            row.add(definitionField, BorderLayout.CENTER);
            row.add(dateLabel, BorderLayout.EAST);

            JPanel rightPanel = new JPanel(new BorderLayout());

            rightPanel.add(dateLabel, BorderLayout.CENTER);
            rightPanel.add(buttonPanel, BorderLayout.EAST);

            row.add(rightPanel, BorderLayout.EAST);

            wordPanel.add(row);
            wordPanel.add(
                Box.createRigidArea(new Dimension(0, 10))
            );
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