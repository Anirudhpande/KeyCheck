import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class GroqProvider implements Provider {

    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public List<String> getModels(API api) throws Exception {

        String apiKey = Encrypt.decrypt(
                api.getAPI(),
                api.getSecretKey()
        );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        "https://api.groq.com/openai/v1/models"
                                )
                        )
                        .header(
                                api.getAuthHeader(),
                                api.getAuthPrefix() + apiKey
                        )
                        .GET()
                        .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() < 200 ||
                response.statusCode() >= 300) {

            throw new Exception(
                    "Failed to fetch Groq models. Status: "
                            + response.statusCode()
                            + "\n"
                            + response.body()
            );
        }

        return parseModels(response.body());
    }

    private List<String> parseModels(String responseBody) {

        JsonObject json =
                JsonParser.parseString(responseBody)
                        .getAsJsonObject();

        JsonArray data =
                json.getAsJsonArray("data");

        List<String> models =
                new ArrayList<>();

        for (JsonElement element : data) {

            JsonObject model =
                    element.getAsJsonObject();

            models.add(
                    model.get("id").getAsString()
            );
        }

        return models;
    }


    @Override
    public String sendRequest(
            API api,
            String selectedModel,
            String prompt
    ) throws Exception {

        String apiKey = Encrypt.decrypt(
                api.getAPI(),
                api.getSecretKey()
        );


        JsonObject json =
                new JsonObject();

        json.addProperty(
                "model",
                selectedModel
        );

        JsonArray messages =
                new JsonArray();

        JsonObject message =
                new JsonObject();

        message.addProperty(
                "role",
                "user"
        );

        message.addProperty(
                "content",
                prompt
        );

        messages.add(message);

        json.add(
                "messages",
                messages
        );


        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        "https://api.groq.com/openai/v1/chat/completions"
                                )
                        )
                        .header(
                                api.getAuthHeader(),
                                api.getAuthPrefix() + apiKey
                        )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        json.toString()
                                )
                        )
                        .build();


        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        System.out.println(
                "Status code: " + response.statusCode()
        );


        if (response.statusCode() < 200 ||
                response.statusCode() >= 300) {

            throw new Exception(
                    "Groq request failed. Status: "
                            + response.statusCode()
                            + "\n"
                            + response.body()
            );
        }



        return parseResponse(
                response.body()
        );
    }


    private String parseResponse(String responseBody) {

        JsonObject json =
                JsonParser.parseString(responseBody)
                        .getAsJsonObject();

        JsonArray choices =
                json.getAsJsonArray("choices");

        if (choices == null ||
                choices.isEmpty()) {

            return "No response generated";
        }

        JsonObject choice =
                choices
                        .get(0)
                        .getAsJsonObject();

        JsonObject message =
                choice.getAsJsonObject("message");

        if (message == null) {
            return "No response message found";
        }

        if (!message.has("content") ||
                message.get("content").isJsonNull()) {

            return "No response content found";
        }

        return message
                .get("content")
                .getAsString();
    }
}