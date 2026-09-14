import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Responses {

    public static void processApi(Accepted_api acceptedApi) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Prompt:");
        String prompt = scanner.nextLine();

        String body = acceptedApi.getBody().replace("Reply with OK", prompt);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(acceptedApi.getUrl()))
                .header(
                        acceptedApi.getAuthHeader(),
                        acceptedApi.getAuthPrefix() + acceptedApi.getAPI()
                )
                .header("Content-Type", "application/json")
                .POST(
                        HttpRequest.BodyPublishers.ofString(body)
                );

        acceptedApi.getExtraheaders()
                .forEach(builder::header);

        try {
            HttpResponse<String> response =
                    HttpClient.newHttpClient()
                            .send(
                                    builder.build(),
                                    HttpResponse.BodyHandlers.ofString()
                            );

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

            String answer = json
                    .getAsJsonArray("candidates")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0)
                    .getAsJsonObject()
                    .get("text")
                    .getAsString();

            System.out.println(answer);

        } catch (Exception exception) {
            System.out.println(
                    "Request failed: " + exception.getMessage()
            );
        }
    }
}