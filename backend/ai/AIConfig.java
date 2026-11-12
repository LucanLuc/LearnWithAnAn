package ai;

import java.nio.file.Path;

public final class AIConfig {

    private static final String API_KEY_ENV = "POLLINATIONS_API_KEY";

    private static final String API_URL =
            "https://gen.pollinations.ai/image/";

    private static final String IMAGE_MODEL =
            "black-forest-labs/flux.1-schnell";

    private static final Path IMAGE_DIRECTORY =
            Path.of("images");

    private AIConfig() {
    }

    public static String getApiKey() {

        String apiKey = System.getenv(API_KEY_ENV);

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Missing POLLINATIONS_API_KEY environment variable."
            );
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