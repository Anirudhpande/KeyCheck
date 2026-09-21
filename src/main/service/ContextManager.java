package src.main.service;

import src.main.models.ContextBudget;
import src.main.models.Message;
import src.main.models.ModelInfo;

import java.util.List;

public class ContextManager {

    private static final double COMPACTION_THRESHOLD = 0.75;
    private static final long DEFAULT_RESERVED_OUTPUT = 2048;

    private final TokenEstimator tokenEstimator;

    public ContextManager(TokenEstimator tokenEstimator) {
        this.tokenEstimator = tokenEstimator;
    }

    public ContextBudget calculateBudget(
            ModelInfo model,
            List<Message> messages
    ) throws Exception {

        long estimatedInputTokens =
                tokenEstimator.estimate(
                        model.getModelId(),
                        messages
                );

        long reservedOutput =
                model.getMaxOutputTokens() > 0
                        ? model.getMaxOutputTokens()
                        : DEFAULT_RESERVED_OUTPUT;

        long availableInputBudget =
                model.getMaxContextTokens()
                        - reservedOutput;

        long threshold =
                (long) (
                        availableInputBudget
                                * COMPACTION_THRESHOLD
                );

        return new ContextBudget(
                model.getMaxContextTokens(),
                reservedOutput,
                availableInputBudget,
                threshold,
                estimatedInputTokens
        );
    }
}