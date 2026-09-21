package src.main.test;

import src.main.models.ContextBudget;
import src.main.models.Message;
import src.main.models.ModelInfo;
import src.main.models.ModelRegistry;
import src.main.service.ContextManager;
import src.main.service.HeuristicTokenEstimator;
import src.main.service.TokenEstimator;

import java.util.List;

public class ContextTest {

    public static void main(String[] args) throws Exception {

        ModelInfo model =
                ModelRegistry.getModel(
                        "openai",
                        "unknown-model"
                );

        TokenEstimator estimator =
                new HeuristicTokenEstimator();

        ContextManager contextManager =
                new ContextManager(estimator);

        StringBuilder text = new StringBuilder();

        for(int i = 0; i<20000; i++){
            text.append("This is test content for vectra context management");
        }

        List<Message> messages =
                List.of(
                        new Message(
                                "user",
                                text.toString()
                        )
                );

        ContextBudget budget =
                contextManager.calculateBudget(
                        model,
                        messages
                );

        System.out.println(
                "Max context: "
                        + budget.getMaxContextTokens()
        );

        System.out.println(
                "Reserved output: "
                        + budget.getReservedOutputTokens()
        );

        System.out.println(
                "Available input: "
                        + budget.getAvailableInputTokens()
        );

        System.out.println(
                "Compaction threshold: "
                        + budget.getCompactionThreshold()
        );

        System.out.println(
                "Estimated input: "
                        + budget.getEstimatedInputTokens()
        );

        System.out.println(
                "Remaining: "
                        + budget.getRemainingInputTokens()
        );

        System.out.println(
                "Should compact: "
                        + budget.shouldCompact()
        );
    }
}