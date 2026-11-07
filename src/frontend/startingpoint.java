package frontend; 

public class startingpoint {
	public static void main(String[] args) {
		String apiKey = System.getenv("OPENAI_API_KEY"); 
		
		System.out.println("API KEY STATUS: " + (apiKey == null ? "NOT FOUND" : "FOUND")); 
	new Homepage(); 
	}
}