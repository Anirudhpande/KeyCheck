import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ModelSelector {

    public static List<String> getModels(API api) throws Exception{

        String apiKey = Encrypt.decrypt(api.getAPI(), api.getSecretKey());

        String modelsUrl;

        if(api.getProvider_name().equalsIgnoreCase("OpenAI")){
            modelsUrl = "https://api.openai.com/v1/models";
        } else if (api.getProvider_name().equalsIgnoreCase("Gemini")) {
            modelsUrl = "https://generativelanguage.googleapis.com/v1beta/models";
        } else if (api.getProvider_name().equalsIgnoreCase("Anthropic")) {
            modelsUrl = "https://api.anthropic.com/v1/models";
        } else if (api.getProvider_name().equalsIgnoreCase("Groq")) {
            modelsUrl = "https://api.groq.com/openai/v1/models";
        } else{
            throw new Exception("Provider not supported yet");
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(modelsUrl))
                .GET();

        if(api.getProvider_name().equalsIgnoreCase("OpenAI") || api.getProvider_name().equalsIgnoreCase("Groq")){
            builder.header(
                    api.getAuthHeader(), api.getAuthPrefix() + apiKey
            );
        } else if (api.getProvider_name().equalsIgnoreCase("Gemini")) {
            builder.header(
                    "x-goog-api-key", apiKey
            );
        } else if (api.getProvider_name().equalsIgnoreCase("Anthropic")) {
            builder.header(
                    "x-api-key",
                    apiKey
            );
        }


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(modelsUrl))
                .header(
                        api.getAuthHeader(),
                        api.getAuthPrefix() + apiKey
                )
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        builder.build(),
                        HttpResponse.BodyHandlers.ofString()
                );

        if(response.statusCode() !=200){
            throw new Exception("Failed to fetch models. Status code: " + response.statusCode());
        }

        if (api.getProvider_name().equalsIgnoreCase("OpenAI")) {
            return parseOpenAIModels(response.body());
        } else if (api.getProvider_name().equalsIgnoreCase("Gemini")) {
            return parseGeminiModels(response.body());
        } else if (api.getProvider_name().equalsIgnoreCase("Claude")) {
            return parseClaudeModels(response.body());
        } else if (api.getProvider_name().equalsIgnoreCase("Groq")){
            return parseOpenAIModels(response.body());
        }
        throw new Exception("Provider not supported yet");
    }

    private static List<String> parseOpenAIModels(String responseBody){
        JsonObject json = JsonParser.parseString(responseBody)
                .getAsJsonObject();

        JsonArray data = json.getAsJsonArray("data");

        List<String> models = new ArrayList<>();

        for(JsonElement element: data){

            JsonObject model = element.getAsJsonObject();

            models.add(
                    model.get("id").getAsString()
            );
        }

        return models;
    }

    private static List<String> parseGeminiModels(String responseBody) {

        JsonObject json =
                JsonParser.parseString(responseBody)
                        .getAsJsonObject();

        JsonArray models =
                json.getAsJsonArray("models");

        List<String> modelIds = new ArrayList<>();

        for (JsonElement element : models) {

            JsonObject model =
                    element.getAsJsonObject();

            String name =
                    model.get("name").getAsString();

            modelIds.add(name);
        }

        return modelIds;
    }

    private static List<String> parseClaudeModels(String responseBody) {

        JsonObject json =
                JsonParser.parseString(responseBody)
                        .getAsJsonObject();

        JsonArray data =
                json.getAsJsonArray("data");

        List<String> models = new ArrayList<>();

        for (JsonElement element : data) {

            JsonObject model =
                    element.getAsJsonObject();

            models.add(
                    model.get("id").getAsString()
            );
        }

        return models;
    }

    public static String selectModel(API api) throws Exception{
        List<String> models = getModels(api);

        if(models.isEmpty()){
            throw new Exception("No models available");
        }

        System.out.println("/nAvailable Models:");

        for(int i = 0; i<models.size(); i++){
            System.out.println((i+1) + ". " + models.get(i));
        }

        Scanner scanner = new Scanner(System.in);

        int choice = scanner.nextInt();

        System.out.println("\nSelect Model");

        if(choice < 1 || choice > models.size()){
            throw new IllegalArgumentException("Invalid model selection");
        }

        return models.get(choice-1);
    }

}
