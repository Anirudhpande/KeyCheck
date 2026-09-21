package src.main.models;

public class ModelInfo {

    private final String provider;
    private final String modelId;
    private final long maxContextTokens;
    private final long maxOutputTokens;

    public ModelInfo(
            String provider,
            String modelId,
            long maxContextTokens,
            long maxOutputTokens
    ) {
        this.provider = provider;
        this.modelId = modelId;
        this.maxContextTokens = maxContextTokens;
        this.maxOutputTokens = maxOutputTokens;
    }

    public String getProvider() {
        return provider;
    }

    public String getModelId() {
        return modelId;
    }

    public long getMaxContextTokens() {
        return maxContextTokens;
    }

    public long getMaxOutputTokens() {
        return maxOutputTokens;
    }
}