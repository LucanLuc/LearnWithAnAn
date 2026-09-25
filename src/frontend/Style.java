package frontend;
import java.awt.*; 
import javax.swing.*;

public class Style {
	
	public static void styleButton(JButton button) {
		Dimension buttonSize = new Dimension(200,100); 
		button.setFont(new Font("Times New Roman", Font.BOLD, 18));
		button.setAlignmentX(Component.CENTER_ALIGNMENT); 
		button.setBackground(new Color(52, 152, 219));
		button.setForeground(Color.BLACK);
		button.setFocusPainted(false); 
		button.setMaximumSize(buttonSize);
		
	}
	
	public static void styleHeading1(JLabel heading) {
		heading.setFont(new Font("Times New Roman", Font.BOLD, 50));
		heading.setAlignmentX(Component.CENTER_ALIGNMENT);
	}
	
	public static void stylePage(JFrame frame) {
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
	}
}
