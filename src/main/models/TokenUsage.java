package src.main.models;

public class TokenUsage {

    private final long inputTokens;
    private final long outputTokens;
    private final long cachedInputTokens;
    private final long totalTokens;

    public TokenUsage(
            long inputTokens,
            long outputTokens,
            long cachedInputTokens,
            long totalTokens
    ){
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
        this.cachedInputTokens = cachedInputTokens;
        this.totalTokens = totalTokens;
    }

    public long getInputTokens(){
        return inputTokens;
    }

    public long getCachedInputTokens() {
        return cachedInputTokens;
    }

    public long getOutputTokens() {
        return outputTokens;
    }

    public long getTotalTokens() {
        return totalTokens;
    }
}
