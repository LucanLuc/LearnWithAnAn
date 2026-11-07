package ai;

import java.nio.file.Path; 

public class AIConfig {
	private static final String API_KEY_ENV = "OPENAI_API_KEY"; 
	private static final String API_URL = "https://api.openai.com/v1/images/generations"; 
	private static final String IMAGE_MODEL = "gpt-image-2.5-flare";
	
	//local image storage
	private static final Path IMAGE_DIRECTORY = Path.of("images"); 
	private AIConfig() {
	}
	
	public static String getApiKey() {
		String apiKey = System.getenv(API_KEY_ENV); 
		if (apiKey  == null || apiKey.isBlank()) {
			throw new IllegalStateException("Missing OPENAI_API_KEY environment variable."); 
		}
		
		return apiKey; 
	}
	
	public static String getApiUrl() {
		return API_URL; 
	}
	
	public static String getImageModel() {
		return IMAGE_MODEL; 
	}
	
	public static Path getImageDirectory() {
		return IMAGE_DIRECTORY; 
	}
}
