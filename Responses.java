import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Responses {

    public static void processApi(API acceptedApi, String selectedModel) throws Exception {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Prompt:");
        String prompt = scanner.nextLine();

        String body;

        if (acceptedApi.getProvider_name().equalsIgnoreCase("OpenAI")) {

            JsonObject json = new JsonObject();

            json.addProperty("model", selectedModel);
            json.addProperty("input", prompt);

            body = json.toString();

        } else if (acceptedApi.getProvider_name().equalsIgnoreCase("Groq")) {
            JsonObject json = new JsonObject();

            json.addProperty("model", selectedModel);

            JsonArray messages = new JsonArray();

            JsonObject message = new JsonObject();
            message.addProperty("role", "user");
            message.addProperty("content", prompt);

            messages.add(message);

            json.add("messages", messages);

            body = json.toString();

        } else if (acceptedApi.getProvider_name().equalsIgnoreCase("Anthropic")) {
            JsonObject json = new JsonObject();

            json.addProperty("model", selectedModel);
            json.addProperty("max_tokens", 1024);

            JsonArray messages = new JsonArray();

            JsonObject message = new JsonObject();
            message.addProperty("role", "user");
            message.addProperty("content", prompt);

            messages.add(message);

            json.add("messages", messages);

            body = json.toString();

        } else {

            body = acceptedApi.getBody().replace("Reply with OK", prompt);
        }

        String url = acceptedApi.getUrl();

        if(acceptedApi.getProvider_name().equalsIgnoreCase("Gemini")){
            url = "https://generativelanguage.googleapis.com/v1beta/" + selectedModel + ":generateContent";
        }


        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(
                        acceptedApi.getAuthHeader(),
                        acceptedApi.getAuthPrefix() +
                                Encrypt.decrypt(
                                        acceptedApi.getAPI(),
                                        acceptedApi.getSecretKey()
                                )
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
            System.out.println("Status code: " + response.statusCode());
//            System.out.println("Status code: " + response.statusCode());
            System.out.println("Response body:");
            System.out.println(response.body());

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

        JsonArray output = json.getAsJsonArray("output");

        for (JsonElement element : output) {

            JsonObject outputItem = element.getAsJsonObject();

            if (!outputItem.has("content")) {
                continue;
            }

            JsonArray content = outputItem.getAsJsonArray("content");

            for (JsonElement contentElement : content) {

                JsonObject contentItem = contentElement.getAsJsonObject();

                if (contentItem.has("text")) {
                    return contentItem.get("text").getAsString();
                }
            }
        }

        return "No text response found";
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
