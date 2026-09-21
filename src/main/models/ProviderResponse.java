package src.main.models;

public class ProviderResponse {

    private final String content;
    private final TokenUsage usage;

    public ProviderResponse(
            String content,
            TokenUsage usage
    ){
        this.content = content;
        this.usage = usage;
    }

    public String getContent() {
        return content;
    }

    public TokenUsage getUsage() {
        return usage;
    }
}
