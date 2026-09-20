package src.main.providers;

import java.util.HashMap;
import java.util.Map;

public class ProviderRegistry {

    private static final Map<String, Provider> providers = new HashMap<>();

    static {
        providers.put("openai", new OpenAIProvider());
        providers.put("gemini", new GeminiProvider());
        providers.put("groq", new GroqProvider());
        providers.put("anthropic", new AnthropicProvider());
    }

    public static Provider getProvider(String providerName){
        Provider provider = providers.get(providerName.toLowerCase());

        if(provider == null){
            throw new IllegalArgumentException("Unsupported Provuder" + providerName);

        }

        return provider;
    }
}
