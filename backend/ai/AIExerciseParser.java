
package ai;

import model.AIExercise;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AIExerciseParser {

    public AIExercise parse(String response) {

        if (response == null || response.isBlank()) {
            throw new IllegalArgumentException(
                    "AI response cannot be empty."
            );
        }

        String question = extract(
                response,
                "QUESTION:",
                "A:"
        );

        String optionA = extract(response, "A:", "B:");
        String optionB = extract(response, "B:", "C:");
        String optionC = extract(response, "C:", "D:");
        String optionD = extract(response, "D:", "CORRECT ANSWER:");

        String correctAnswer = extract(
                response,
                "CORRECT ANSWER:",
                "EXPLANATION:"
        ).trim().toUpperCase();

        String explanation = extract(
                response,
                "EXPLANATION:",
                "DIFFICULTY:"
        );

        String difficulty = extract(
                response,
                "DIFFICULTY:",
                null
        );

        if (question.isBlank()
                || optionA.isBlank()
                || optionB.isBlank()
                || optionC.isBlank()
                || optionD.isBlank()
                || explanation.isBlank()) {

            throw new IllegalArgumentException(
                    "AI response is missing required exercise fields."
            );
        }

        if (!correctAnswer.matches("[ABCD]")) {
            throw new IllegalArgumentException(
                    "AI returned an invalid correct answer."
            );
        }

        return new AIExercise(
                question,
                new String[] {
                        optionA, optionB, optionC, optionD
                },
                correctAnswer,
                explanation,
                difficulty
        );
    }

    private String extract(
            String text,
            String startMarker,
            String endMarker) {

        int start = text.indexOf(startMarker);

        if (start == -1) {
            return "";
        }

        start += startMarker.length();

        int end;

        if (endMarker == null) {
            end = text.length();
        } else {
            end = text.indexOf(endMarker, start);

            if (end == -1) {
                return "";
            }
        }

        return text.substring(start, end).trim();
    }
}
