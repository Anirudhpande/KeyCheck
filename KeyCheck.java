import java.sql.SQLOutput;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.crypto.SecretKey;

public class KeyCheck {

    public static void main(String[] args) throws Exception {

        SecretKey secretKey = Encrypt.generateKey();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter API Provider Name:");
        String providerName = scanner.nextLine();

        System.out.println("Please Enter your API KEY");
        String API_KEY = scanner.nextLine();

        String encryptedAPI =
                Encrypt.encrypt(API_KEY, secretKey);

        List<API> apis = List.of(
                new API(
                        "OpenAI",
                        "https://api.openai.com/v1/responses",
                        "Authorization",
                        "Bearer ",
                        """
                        {"model":"gpt-5.6","input":"Reply with OK"}
                        """,
                        Map.of(),
                        encryptedAPI,
                        secretKey
                ),

                new API(
                        "Anthropic",
                        "https://api.anthropic.com/v1/messages",
                        "x-api-key",
                        "",
                        """
                        {
                          "model":"claude-sonnet-4-20250514",
                          "max_tokens":1,
                          "messages":[
                            {
                              "role":"user",
                              "content":"Reply with OK"
                            }
                          ]
                        }
                        """,
                        Map.of(
                                "anthropic-version",
                                "2023-06-01"
                        ),
                        encryptedAPI,
                        secretKey
                ),

                new API(
                        "Gemini",
                        "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent",
                        "x-goog-api-key",
                        "",
                        """
                        {
                          "contents":[
                            {
                              "parts":[
                                {
                                  "text":"Reply with OK"
                                }
                              ]
                            }
                          ]
                        }
                        """,
                        Map.of(),
                        encryptedAPI,
                        secretKey
                ),

                new API(
                        "Groq",
                        "https://api.groq.com/openai/v1/chat/completions",
                        "Authorization",
                        "Bearer ",
                        """
                        {
                          "model":"openai/gpt-oss-20b",
                          "messages":[
                            {
                              "role":"user",
                              "content":"Reply with OK"
                            }
                          ]
                        }
                        """,
                        Map.of(),
                        encryptedAPI,
                        secretKey
                )
        );

        API api = apis.stream()
                .filter(item ->
                        item.getProvider_name()
                                .equalsIgnoreCase(providerName)
                )
                .findFirst()
                .orElse(null);

        if (api == null) {
            System.out.println("Unsupported Provider");
            return;
        }

        Provider provider;

        switch (providerName.toLowerCase()) {

            case "openai":
                provider = new OpenAIProvider();
                break;

            case "gemini":
                provider = new GeminiProvider();
                break;

            case "groq":
                provider = new GroqProvider();
                break;

            case "anthropic":
                provider = new AnthropicProvider();
                break;

            default:
                System.out.println("Unsupported Provider");
                return;
        }

        List<String> models =
                provider.getModels(api);

        String selectedModel =
                ModelSelection.selectModel(
                        models,
                        scanner
                );
        while (true) {

            System.out.print("\nYou: ");

            String prompt = scanner.nextLine();

            if (prompt.equalsIgnoreCase("exit")) {
                break;
            }

            String answer =
                    provider.sendRequest(
                            api,
                            selectedModel,
                            prompt
                    );

            System.out.println("\nAI: " + answer);
        }



        scanner.close();
    }
}