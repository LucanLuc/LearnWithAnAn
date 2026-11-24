
package ai;

public class OllamaConnectionTest {

    public static void main(String[] args) {

        System.out.println("1. JAVA PROGRAM STARTED");

        try {
            System.out.println("2. CREATING AI CLIENT...");

            AITextClient ai = new AITextClient();

            System.out.println("3. AI CLIENT CREATED!");

            String prompt = "Translate abundant into Vietnamese.";

            System.out.println("4. SENDING REQUEST TO OLLAMA...");

            String answer = ai.generateText(prompt);

            System.out.println("5. RESPONSE RECEIVED:");
            System.out.println(answer);

        } catch (Exception e) {
            System.out.println("ERROR DETECTED:");
            e.printStackTrace();
        }

        System.out.println("6. TEST FINISHED");
    }
}
