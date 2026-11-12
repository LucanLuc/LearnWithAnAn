package frontend;

import javax.swing.*;

import Backend.VocabularyService;

import java.awt.*; 

public class Homepage {
	private VocabularyService service; 
		public Homepage() {
			this(new VocabularyService()); 
		}
		public Homepage(VocabularyService service) {
			this.service = service;
		
		
		JFrame frame = new JFrame("Homepage"); 
		Style.stylePage(frame);
		
		JPanel panel = new JPanel(); 
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); 
		
		JLabel heading1 = new JLabel("Learn with An An");
		Style.styleHeading1(heading1); 
		
		JButton addWordBtn = new JButton("Add Word"); 
		Style.styleButton(addWordBtn); 
		addWordBtn.addActionListener(e -> {
			System.out.println("addword chosen"); 
			new addWordPage(service); 
			frame.dispose(); 
		}); 
		
		JButton reviewBtn = new JButton("Review");
		Style.styleButton(reviewBtn);
		reviewBtn.addActionListener(e -> {
			new reviewPage(service); 
			System.out.println("review chosen");
		}); 
		
		
		JButton searchBtn = new JButton("Search"); 
		Style.styleButton(searchBtn);
		searchBtn.addActionListener(e -> {
			System.out.println("search chosen"); 
		}); 
		
		
		
		JButton dictionaryBtn = new JButton("My Dictionary"); 
		Style.styleButton(dictionaryBtn);
		dictionaryBtn.addActionListener(e-> {
			new deleteWord(service);
			frame.dispose(); 
			System.out.println("deleteButton chosen"); 
		}); 
				
		panel.add(Box.createVerticalGlue());
		panel.add(heading1); 
		panel.add(Box.createRigidArea(new Dimension(0,40)));
		panel.add(addWordBtn); 
		panel.add(Box.createRigidArea(new Dimension(0,20)));
		panel.add(reviewBtn);
		panel.add(Box.createRigidArea(new Dimension(0,20))); 
		panel.add(dictionaryBtn); 
		panel.add(Box.createRigidArea(new Dimension(0,20))); 
		panel.add(Box.createVerticalGlue()); 
		
		frame.add(panel); 
		frame.setVisible(true);

	}
}
