import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.crypto.SecretKey;

public class KeyCheck {

    public static void main(String[] args) throws Exception {

        SecretKey secretKey =
                Encrypt.generateKey();

        Scanner scanner =
                new Scanner(System.in);

        System.out.println("Enter API Provider Name:");
        String providerName =
                scanner.nextLine();

        System.out.println("Please Enter your API KEY");
        String API_KEY =
                scanner.nextLine();

        String encryptedAPI =
                Encrypt.encrypt(
                        API_KEY,
                        secretKey
                );

        API api;

        Provider provider;

        switch (providerName.toLowerCase()) {

            case "openai":

                api = new API(
                        "OpenAI",
                        "Authorization",
                        "Bearer ",
                        Map.of(),
                        encryptedAPI,
                        secretKey
                );

                provider = new OpenAIProvider();

                break;

            case "anthropic":

                api = new API(
                        "Anthropic",
                        "x-api-key",
                        "",
                        Map.of(
                                "anthropic-version",
                                "2023-06-01"
                        ),
                        encryptedAPI,
                        secretKey
                );

                provider = new AnthropicProvider();

                break;

            case "gemini":

                api = new API(
                        "Gemini",
                        "x-goog-api-key",
                        "",
                        Map.of(),
                        encryptedAPI,
                        secretKey
                );

                provider = new GeminiProvider();

                break;

            case "groq":

                api = new API(
                        "Groq",
                        "Authorization",
                        "Bearer ",
                        Map.of(),
                        encryptedAPI,
                        secretKey
                );

                provider = new GroqProvider();

                break;

            default:

                System.out.println(
                        "Unsupported Provider"
                );

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

            String prompt =
                    scanner.nextLine();

            if (prompt.equalsIgnoreCase("exit")) {
                break;
            }

            String answer =
                    provider.sendRequest(
                            api,
                            selectedModel,
                            prompt
                    );

            System.out.println(
                    "\nAI: " + answer
            );
        }

        scanner.close();
    }
}