package src.main.providers;

import src.main.models.ProviderConfig;

import java.util.HashMap;
import java.util.Map;

public class ProviderRegistry {

    private static final Map<String, Provider> providers =
            new HashMap<>();

    private static final Map<String, ProviderConfig> configs =
            new HashMap<>();

    static {
        providers.put("openai", new OpenAIProvider());
        providers.put("gemini", new GeminiProvider());
        providers.put("groq", new GroqProvider());
        providers.put("anthropic", new AnthropicProvider());

        configs.put(
                "openai",
                new ProviderConfig(
                        "OpenAI",
                        "Authorization",
                        "Bearer ",
                        Map.of()
                )
        );

        configs.put(
                "gemini",
                new ProviderConfig(
                        "Gemini",
                        "x-goog-api-key",
                        "",
                        Map.of()
                )
        );

        configs.put(
                "groq",
                new ProviderConfig(
                        "Groq",
                        "Authorization",
                        "Bearer ",
                        Map.of()
                )
        );

        configs.put(
                "anthropic",
                new ProviderConfig(
                        "Anthropic",
                        "x-api-key",
                        "",
                        Map.of(
                                "anthropic-version",
                                "2023-06-01"
                        )
                )
        );
    }

    public static Provider getProvider(String providerName) {

        Provider provider =
                providers.get(
                        providerName.toLowerCase()
                );

        if (provider == null) {
            throw new IllegalArgumentException(
                    "Unsupported Provider: " + providerName
            );
        }

        return provider;
    }

    public static ProviderConfig getConfig(String providerName) {

        ProviderConfig config =
                configs.get(
                        providerName.toLowerCase()
                );

        if (config == null) {
            throw new IllegalArgumentException(
                    "Unsupported Provider: " + providerName
            );
        }

        return config;
    }
}