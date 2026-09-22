package src.main.service;

import src.main.models.API;
import src.main.models.ContextBudget;
import src.main.models.ConversationContext;
import src.main.models.Message;
import src.main.models.ModelInfo;
import src.main.models.ProviderResponse;
import src.main.models.TokenUsage;
import src.main.providers.Provider;
import src.main.ui.ModelSelection;

import java.util.List;
import java.util.Scanner;

public class ChatService {

    private final Provider provider;
    private final API api;
    private final Scanner scanner;
    private final ContextManager contextManager;

    public ChatService(
            Provider provider,
            API api,
            Scanner scanner,
            ContextManager contextManager
    ) {
        this.provider = provider;
        this.api = api;
        this.scanner = scanner;
        this.contextManager = contextManager;
    }

    public void start() throws Exception {

        List<String> models =
                provider.getModels(api);

        String selectedModel =
                ModelSelection.selectModel(
                        models,
                        scanner
                );

        ModelInfo model =
                provider.getModelInfo(
                        api,
                        selectedModel
                );

        System.out.println(
                "\nModel Context: "
                        + model.getMaxContextTokens()
        );

        System.out.println(
                "Model Max Output: "
                        + model.getMaxOutputTokens()
        );

        Summarizer summarizer =
                new ProviderSummarizer(
                        provider,
                        api,
                        selectedModel
                );

        CompactionService compactionService =
                new CompactionService(
                        summarizer,
                        contextManager.getTokenEstimator()
                );

        ConversationContext context =
                new ConversationContext();

        while (true) {

            System.out.print("\nYou: ");

            String prompt =
                    scanner.nextLine();

            if (prompt.equalsIgnoreCase("exit")) {
                break;
            }

            context.addMessage(
                    new Message(
                            "user",
                            prompt
                    )
            );

            /*
             * Calculate the current context budget.
             */
            ContextBudget budget =
                    contextManager.calculateBudget(
                            model,
                            context.getMessagesWithSummary()
                    );

            /*
             * If the context has crossed the threshold,
             * compact the older conversation.
             */
            if (budget.shouldCompact()) {

                System.out.println(
                        "\nContext limit approaching. "
                                + "Compacting conversation..."
                );

                long targetTokens =
                        budget.getAvailableInputTokens() / 2;

                compactionService.compact(
                        context,
                        targetTokens
                );

                /*
                 * Recalculate after compaction.
                 */
                budget =
                        contextManager.calculateBudget(
                                model,
                                context.getMessagesWithSummary()
                        );

                System.out.println(
                        "Context after compaction: "
                                + budget.getEstimatedInputTokens()
                                + " estimated tokens"
                );
            }

            ProviderResponse response =
                    provider.sendRequest(
                            api,
                            selectedModel,
                            context.getMessagesWithSummary()
                    );

            context.addMessage(
                    new Message(
                            "assistant",
                            response.getContent()
                    )
            );

            System.out.println(
                    "\nAI: " + response.getContent()
            );

            TokenUsage usage =
                    response.getUsage();

            System.out.println(
                    "\nTokens Used"
            );

            System.out.println(
                    "Input  : " + usage.getInputTokens()
            );

            System.out.println(
                    "Output : " + usage.getOutputTokens()
            );

            System.out.println(
                    "Cached : " + usage.getCachedInputTokens()
            );

            System.out.println(
                    "Total  : " + usage.getTotalTokens()
            );
        }
    }
}