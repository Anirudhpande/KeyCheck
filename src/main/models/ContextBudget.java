package src.main.models;

public class ContextBudget {

    private final long maxContextTokens;
    private final long reservedOutputTokens;
    private final long availableInputTokens;
    private final long compactionThreshold;
    private final long estimatedInputTokens;

    public ContextBudget(
            long maxContextTokens,
            long reservedOutputTokens,
            long availableInputTokens,
            long compactionThreshold,
            long estimatedInputTokens
    ) {
        this.maxContextTokens = maxContextTokens;
        this.reservedOutputTokens = reservedOutputTokens;
        this.availableInputTokens = availableInputTokens;
        this.compactionThreshold = compactionThreshold;
        this.estimatedInputTokens = estimatedInputTokens;
    }

    public long getMaxContextTokens() {
        return maxContextTokens;
    }

    public long getReservedOutputTokens() {
        return reservedOutputTokens;
    }

    public long getAvailableInputTokens() {
        return availableInputTokens;
    }

    public long getCompactionThreshold() {
        return compactionThreshold;
    }

    public long getEstimatedInputTokens() {
        return estimatedInputTokens;
    }

    public boolean shouldCompact() {
        return estimatedInputTokens >= compactionThreshold;
    }

    public long getRemainingInputTokens() {
        return Math.max(
                0,
                availableInputTokens - estimatedInputTokens
        );
    }
}