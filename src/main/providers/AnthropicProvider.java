package src.main.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import src.main.models.API;
import src.main.models.Message;
import src.main.models.ModelInfo;
import src.main.models.ProviderResponse;
import src.main.models.TokenUsage;
import src.main.security.Encrypt;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class AnthropicProvider implements Provider {

    private final HttpClient client =
            HttpClient.newHttpClient();

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
                                        "https://api.anthropic.com/v1/models"
                                )
                        )
                        .header(
                                "x-api-key",
                                apiKey
                        )
                        .header(
                                "anthropic-version",
                                "2023-06-01"
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
                    "Failed to fetch Anthropic models. Status: "
                            + response.statusCode()
                            + "\n"
                            + response.body()
            );
        }

        return parseModels(response.body());
    }

    private List<String> parseModels(
            String responseBody
    ) {

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
    public ModelInfo getModelInfo(
            API api,
            String modelId
    ) throws Exception {

        String apiKey =
                Encrypt.decrypt(
                        api.getAPI(),
                        api.getSecretKey()
                );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        "https://api.anthropic.com/v1/models/"
                                                + modelId
                                )
                        )
                        .header(
                                "x-api-key",
                                apiKey
                        )
                        .header(
                                "anthropic-version",
                                "2023-06-01"
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
                    "Failed to fetch Anthropic model info. Status: "
                            + response.statusCode()
                            + "\n"
                            + response.body()
            );
        }

        JsonObject json =
                JsonParser.parseString(
                        response.body()
                ).getAsJsonObject();

        if (!json.has("max_input_tokens") ||
                json.get("max_input_tokens").isJsonNull()) {

            throw new Exception(
                    "Anthropic model does not provide "
                            + "max_input_tokens: "
                            + modelId
            );
        }

        long maxContextTokens =
                json.get("max_input_tokens")
                        .getAsLong();

        long maxOutputTokens = 2048;

        if (json.has("max_tokens") &&
                !json.get("max_tokens").isJsonNull()) {

            maxOutputTokens =
                    json.get("max_tokens")
                            .getAsLong();
        }

        return new ModelInfo(
                "Anthropic",
                modelId,
                maxContextTokens,
                maxOutputTokens
        );
    }

    @Override
    public ProviderResponse sendRequest(
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

        json.addProperty(
                "max_tokens",
                1024
        );

        JsonArray messageArray =
                new JsonArray();

        for (Message message : messages) {

            JsonObject messageObject =
                    new JsonObject();

            messageObject.addProperty(
                    "role",
                    message.getRole()
            );

            messageObject.addProperty(
                    "content",
                    message.getContent()
            );

            messageArray.add(
                    messageObject
            );
        }

        json.add(
                "messages",
                messageArray
        );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        "https://api.anthropic.com/v1/messages"
                                )
                        )
                        .header(
                                "x-api-key",
                                apiKey
                        )
                        .header(
                                "anthropic-version",
                                "2023-06-01"
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
                    "Anthropic request failed. Status: "
                            + response.statusCode()
                            + "\n"
                            + response.body()
            );
        }

        return parseResponse(
                response.body()
        );
    }

    private ProviderResponse parseResponse(
            String responseBody
    ) {

        JsonObject json =
                JsonParser.parseString(responseBody)
                        .getAsJsonObject();

        String answer =
                "No response generated";

        JsonArray content =
                json.getAsJsonArray("content");

        if (content != null &&
                !content.isEmpty()) {

            for (JsonElement element : content) {

                JsonObject contentItem =
                        element.getAsJsonObject();

                if (contentItem.has("text")) {

                    answer =
                            contentItem
                                    .get("text")
                                    .getAsString();

                    break;
                }
            }
        }

        JsonObject usage =
                json.getAsJsonObject("usage");

        long inputTokens =
                usage.get("input_tokens")
                        .getAsLong();

        long outputTokens =
                usage.get("output_tokens")
                        .getAsLong();

        long cachedInputTokens = 0;

        if (usage.has("cache_read_input_tokens") &&
                !usage.get(
                        "cache_read_input_tokens"
                ).isJsonNull()) {

            cachedInputTokens =
                    usage.get(
                            "cache_read_input_tokens"
                    ).getAsLong();
        }

        long totalTokens =
                inputTokens + outputTokens;

        TokenUsage tokenUsage =
                new TokenUsage(
                        inputTokens,
                        outputTokens,
                        cachedInputTokens,
                        totalTokens
                );

        return new ProviderResponse(
                answer,
                tokenUsage
        );
    }
}