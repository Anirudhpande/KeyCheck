import java.util.List;
import java.util.Scanner;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KeyCheck {

    public static void main(String[] args) {

        List<API> data = List.of(

            new API(
                "OpenAI",
                "https://api.openai.com/v1/responses",
                "Authorization"
            ),

            new API(
                "Claude",
                "https://api.anthropic.com/v1/messages",
                "x-api-key"
            ),

            new API(
                "Gemini",
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent",
                "x-goog-api-key"
            ),

            new API(
                "Groq",
                "https://api.groq.com/openai/v1/chat/completions",
                "Authorization"
            )
        );

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter API Provier Name: ");
        String input = scanner.nextLine();

        System.out.println("Please Enter your API KEY");
        String API_KEY = scanner.nextLine();

        HttpClient client = HttpClient.newHttpClient();

        String json = """
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": "Hello"
                        }
                      ]
                    }
                  ]
                }
                """;

        for (API api: data){
            if(api.getname().equalsIgnoreCase(input)){
                HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api.getUrl()))
                .header(api.getAuthHeader(), API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

                try {

                    HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

                    System.out.println("Status Code: " + response.statusCode());
                    System.out.println("Response Body: " + response.body());

                    if (response.statusCode() >= 200 && response.statusCode() < 300) {

                        System.out.println("API request successful");

                    }
                    else {
                        System.out.println("API request failed");
                    }

                } 
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        scanner.close();
    }
}