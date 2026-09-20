import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class OpenAIProvider implements Provider {

    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public List<String> getModels(API api) throws Exception {

        String apiKey =
                Encrypt.decrypt(
                        api.getAPI(),
                        api.getSecretKey()
                );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        "https://api.openai.com/v1/models"
                                )
                        )
                        .header(
                                api.getAuthHeader(),
                                api.getAuthPrefix() + apiKey
                        )
                        .GET()
                        .build();

        HttpResponse<String> response =
                HttpClient.newHttpClient()
                        .send(
                                request,
                                HttpResponse.BodyHandlers.ofString()
                        );

        if (response.statusCode() != 200) {
            throw new Exception(
                    "Failed to fetch OpenAI models: "
                            + response.body()
            );
        }

        return parseModels(response.body());
    }



    private List<String> parseModels(String responseBody) {

        JsonObject json =
                JsonParser.parseString(responseBody)
                        .getAsJsonObject();

        var data =
                json.getAsJsonArray("data");

        List<String> models = new ArrayList<>();

        data.forEach(element -> {

            JsonObject model =
                    element.getAsJsonObject();

            models.add(
                    model.get("id").getAsString()
            );
        });

        return models;
    }

    @Override
    public String sendRequest(
            API api,
            String selectedModel,
            List<Message> messages
    ) throws Exception {

        String apiKey =
                Encrypt.decrypt(
                        api.getAPI(),
                        api.getSecretKey()
                );

        JsonObject json =
                new JsonObject();

        json.addProperty(
                "model",
                selectedModel
        );

        JsonArray input =
                new JsonArray();

        for (Message message : messages) {

            JsonObject item =
                    new JsonObject();

            item.addProperty(
                    "role",
                    message.getRole()
            );

            item.addProperty(
                    "content",
                    message.getContent()
            );

            input.add(item);
        }

        json.add(
                "input",
                input
        );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        "https://api.openai.com/v1/responses"
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
                    "OpenAI request failed: "
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

        if (json.has("output_text")) {

            return json
                    .get("output_text")
                    .getAsString();
        }

        var output =
                json.getAsJsonArray("output");

        for (var element : output) {

            JsonObject outputItem =
                    element.getAsJsonObject();

            if (!outputItem.has("content")) {
                continue;
            }

            var content =
                    outputItem.getAsJsonArray("content");

            for (var contentElement : content) {

                JsonObject contentItem =
                        contentElement.getAsJsonObject();

                if (contentItem.has("text")) {

                    return contentItem
                            .get("text")
                            .getAsString();
                }
            }
        }

        return "No text response found";
    }
}