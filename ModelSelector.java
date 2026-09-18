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
        }
        else{
            throw new Exception("Provider not supported yet");
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
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if(response.statusCode() !=200){
            throw new Exception("Failed to fetch models. Status code: " + response.statusCode());
        }
        return parseOpenAIModels(response.body());
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
