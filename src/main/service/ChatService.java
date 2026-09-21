package src.main.service;

import src.main.models.API;
import src.main.models.Message;
import src.main.models.ProviderResponse;
import src.main.models.TokenUsage;
import src.main.providers.Provider;
import src.main.ui.ModelSelection;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatService {

    private final Provider provider;
    private final API api;
    private final Scanner scanner;

    public ChatService(
            Provider provider,
            API api,
            Scanner scanner
    ) {
        this.provider = provider;
        this.api = api;
        this.scanner = scanner;
    }

    public void start() throws Exception {

        List<String> models =
                provider.getModels(api);

        String selectedModel =
                ModelSelection.selectModel(
                        models,
                        scanner
                );

        List<Message> messages =
                new ArrayList<>();

        while (true) {

            System.out.print("\nYou: ");

            String prompt =
                    scanner.nextLine();

            if (prompt.equalsIgnoreCase("exit")) {
                break;
            }

            messages.add(
                    new Message(
                            "user",
                            prompt
                    )
            );

            ProviderResponse response =
                    provider.sendRequest(
                            api,
                            selectedModel,
                            messages
                    );

            messages.add(
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