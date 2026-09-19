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

public class GeminiProvider implements Provider {

    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public List<String> getModels(API api) throws Exception {

        String apiKey = Encrypt.decrypt(
                api.getAPI(),
                api.getSecretKey()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(
                        URI.create(
                                "https://generativelanguage.googleapis.com/v1beta/models"
                        )
                )
                .header(
                        "x-goog-api-key",
                        apiKey
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
                    "Failed to fetch Gemini models. Status: "
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

        JsonArray models =
                json.getAsJsonArray("models");

        List<String> modelIds = new ArrayList<>();

        for (JsonElement element : models) {

            JsonObject model =
                    element.getAsJsonObject();

            modelIds.add(
                    model.get("name").getAsString()
            );
        }

        return modelIds;
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

        String url =
                "https://generativelanguage.googleapis.com/v1beta/"
                        + selectedModel
                        + ":generateContent";


        JsonObject json = new JsonObject();

        JsonArray contents = new JsonArray();

        JsonObject content = new JsonObject();

        JsonArray parts = new JsonArray();

        JsonObject part = new JsonObject();

        part.addProperty(
                "text",
                prompt
        );

        parts.add(part);

        content.add(
                "parts",
                parts
        );

        contents.add(content);

        json.add(
                "contents",
                contents
        );


        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(
                                "x-goog-api-key",
                                apiKey
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
                    "Gemini request failed. Status: "
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

        JsonArray candidates =
                json.getAsJsonArray("candidates");

        if (candidates == null ||
                candidates.isEmpty()) {

            return "No response generated";
        }

        JsonObject candidate =
                candidates
                        .get(0)
                        .getAsJsonObject();

        JsonObject content =
                candidate.getAsJsonObject(
                        "content"
                );

        if (content == null) {
            return "No response content found";
        }

        JsonArray parts =
                content.getAsJsonArray(
                        "parts"
                );

        if (parts == null ||
                parts.isEmpty()) {

            return "No response text found";
        }

        for (JsonElement element : parts) {

            JsonObject part =
                    element.getAsJsonObject();

            if (part.has("text")) {

                return part
                        .get("text")
                        .getAsString();
            }
        }

        return "No response text found";
    }
}