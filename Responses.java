import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Scanner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Responses {

    public static void processApi(API acceptedApi) throws Exception {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Prompt:");
        String prompt = scanner.nextLine();

        String body = acceptedApi.getBody().replace("Reply with OK", prompt);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(acceptedApi.getUrl()))
                .header(
                        acceptedApi.getAuthHeader(),
                        acceptedApi.getAuthPrefix() + Encrypt.decrypt(acceptedApi.getAPI(), acceptedApi.getSecretKey())
                )
                .header("Content-Type", "application/json")
                .POST(
                        HttpRequest.BodyPublishers.ofString(body)
                );

        acceptedApi.getExtraHeaders()
                .forEach(builder::header);

        try {
            HttpResponse<String> response =
                    HttpClient.newHttpClient()
                            .send(
                                    builder.build(),
                                    HttpResponse.BodyHandlers.ofString()
                            );

            String answer = parseResponse(
                    acceptedApi.getProvider_name(), response.body()
            );

            System.out.println(answer);

        } catch (Exception exception) {
            System.out.println(
                    "Request failed: " + exception.getMessage()
            );
        }
    }

    private static String parseResponse(String provider, String responseBody){
        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

        return switch (provider.toLowerCase()) {
            case "gemini" -> parseGemini(json);
            case "openai" -> parseOpenAI(json);
            case "claude" -> parseClaude(json);
            case "groq" -> parseGroq(json);
            default -> "Unsupported provider";
        };
    }

    private static String parseGemini(JsonObject json) {
        return json
                .getAsJsonArray("candidates")
                .get(0)
                .getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0)
                .getAsJsonObject()
                .get("text")
                .getAsString();
    }

    private static String parseOpenAI(JsonObject json) {

        if (json.has("output_text")) {
            return json.get("output_text").getAsString();
        }

        return json
                .getAsJsonArray("output")
                .get(0)
                .getAsJsonObject()
                .getAsJsonArray("content")
                .get(0)
                .getAsJsonObject()
                .get("text")
                .getAsString();
    }

    private static String parseClaude(JsonObject json) {
        return json
                .getAsJsonArray("content")
                .get(0)
                .getAsJsonObject()
                .get("text")
                .getAsString();
    }

    private static String parseGroq(JsonObject json) {
        return json
                .getAsJsonArray("choices")
                .get(0)
                .getAsJsonObject()
                .getAsJsonObject("message")
                .get("content")
                .getAsString();
    }
}
