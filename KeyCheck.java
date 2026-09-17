import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;


public class KeyCheck {

    public static void main(String[] args) throws Exception {

         List<API> apis = List.of(
            new API(
                "OpenAI",
                "https://api.openai.com/v1/responses",
                "Authorization",
                "Bearer ",
                """
                {"model":"gpt-5.6","input":"Reply with exactly: OK"}
                """,
                Map.of()
            ),
            new API(
                "Claude",
                "https://api.anthropic.com/v1/messages",
                "x-api-key",
                "",
                """
                {
                  "model":"claude-sonnet-4-20250514",
                  "max_tokens":1,
                  "messages":[{"role":"user","content":"Reply with OK"}]
                }
                """,
                Map.of("anthropic-version", "2023-06-01")
            ),
            new API(
                "Gemini",
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent",
                "x-goog-api-key",
                "",
                """
                {
                  "contents":[
                    {"parts":[{"text":"Reply with OK"}]}
                  ]
                }
                """,
                Map.of()
            ),
            new API(
                "Groq",
                "https://api.groq.com/openai/v1/chat/completions",
                "Authorization",
                "Bearer ",
                """
                {
                  "model":"openai/gpt-oss-20b",
                  "messages":[{"role":"user","content":"Reply with OK"}]
                }
                """,
                Map.of()
            )
        );

        SecretKey secretKey = Encrypt.generateKey();

         boolean API_Accepted = false;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter API Provier Name: ");
        String providerName = scanner.nextLine();

        System.out.println("Please Enter your API KEY");
        String API_KEY = scanner.nextLine();

        String EncryptedAPi = Encrypt.encrypt(API_KEY, secretKey);

        API api = apis.stream()
        .filter(item-> item.getProvider_name().equalsIgnoreCase(providerName))
        .findFirst()
        .orElse(null);
        
        if(api == null){
            System.out.println("Unsupported Provider");
            return;
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(api.getUrl()))
            .header(api.getAuthHeader(), api.getAuthPrefix() + API_KEY)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(api.getBody()));

        api.getExtraHeaders().forEach(builder::header);


        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            System.out.println("Status code: " + status);

            if (status >= 200 && status < 300) {
                System.out.println("Key accepted.");
                API_Accepted = true;

            } else if (status == 401) {
                System.out.println("Key is invalid or revoked.");
            } else if (status == 403) {
                System.out.println("Key was recognized, but lacks permission or has restrictions.");
            } else if (status == 429) {
                System.out.println("Key may be valid, but is rate-limited or out of quota.");
            } else {
                System.out.println("Could not determine key validity.");
                System.out.println(response.body());
            }
        } catch (Exception exception) {
            System.out.println("Request failed: " + exception.getMessage());
        }

        System.out.println(EncryptedAPi);

        if(API_Accepted){
            Accepted_api acceptedApi = new Accepted_api(
                    api.getProvider_name(),
                    api.getUrl(),
                    api.getAuthHeader(),
                    api.getAuthPrefix(),
                    api.getBody(),
                    api.getExtraHeaders(),
                    EncryptedAPi,
                    secretKey
            );

            while(true){

                Responses.processApi(acceptedApi);
            }
        }


    }
}