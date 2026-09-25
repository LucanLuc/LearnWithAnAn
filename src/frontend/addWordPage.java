package frontend;
import java.awt.*;
import javax.swing.*;

import Backend.VocabularyService;
import Backend.Word;

public class addWordPage {
	private VocabularyService service;
	public addWordPage(VocabularyService service) {
	    this.service = service;
	    
		JFrame frame = new JFrame("add word page");
		Style.stylePage(frame); 
		
		JPanel panel = new JPanel(); 
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); 
		JLabel title = new JLabel("Vocab"); 
		Style.styleHeading1(title);
	
		
		JLabel H1 = new JLabel("Enter the word: "); 
		JTextField vocabTextField = new JTextField(30);
		vocabTextField.setBounds(50, 50, 100, 30); 
		
					
		JLabel H2 = new JLabel("Enter the definition: "); 
		JTextField defTextField = new JTextField(); 
		defTextField.setBounds(50,50,200,30);
		

		
		 JButton submitBtn = new JButton("Submit");

	        submitBtn.addActionListener(e -> {

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
	            JOptionPane.showMessageDialog(frame, "Word added"); 

	            // Clear text fields after submitting
	            vocabTextField.setText("");
	            defTextField.setText("");

	            // Put cursor back in the word field
	            vocabTextField.requestFocusInWindow();
	        });

	        // Enter in word field → move to definition field
	        vocabTextField.addActionListener(e -> {
	            defTextField.requestFocusInWindow();
	        });

	        // Enter in definition field → automatically click Submit
	        defTextField.addActionListener(e -> {
	            submitBtn.doClick();
	        });

		
		JButton deleteWordBtn = new JButton("Delete Word"); 
		deleteWordBtn.addActionListener(e -> {
			String wordInput = vocabTextField.getText().trim();

			Word wordToDelete = service.searchWord(wordInput);

			if (wordToDelete != null) {

			    service.deleteWord(wordToDelete);

			    JOptionPane.showMessageDialog(
			        frame,
			        "Word deleted successfully."
			    );

			} else {

			    JOptionPane.showMessageDialog(
			        frame,
			        "Word not found."
			    );
			}
		});
			
		
		JButton returnBtn = new JButton("Return"); 
		returnBtn.addActionListener(e -> {
			new Homepage(); 
			frame.dispose(); 
		});
		
			
		JPanel controlPanel = new JPanel(); 
		controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0)); 
		controlPanel.add(submitBtn);
		controlPanel.add(deleteWordBtn); 
		controlPanel.add(returnBtn);
		panel.add(controlPanel); 
		
		panel.add(Box.createVerticalGlue());
		panel.add(title); 
		panel.add(Box.createRigidArea(new Dimension(0,40)));
		panel.add(H1);
		panel.add(vocabTextField); 
		panel.add(Box.createRigidArea(new Dimension(0,40))); 
		panel.add(H2); 
		panel.add(defTextField); 
		panel.add(Box.createRigidArea(new Dimension(0, 40))); 
		panel.add(controlPanel); 
		panel.add(Box.createVerticalGlue()); 
		
		frame.add(panel); 
		frame.setVisible(true); 
	}
}
