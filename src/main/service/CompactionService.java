package src.main.service;

import src.main.models.ConversationContext;
import src.main.models.Message;

import java.util.ArrayList;
import java.util.List;

public class CompactionService {

    private final Summarizer summarizer;
    private final TokenEstimator tokenEstimator;

    public CompactionService(
            Summarizer summarizer,
            TokenEstimator tokenEstimator
    ) {
        this.summarizer = summarizer;
        this.tokenEstimator = tokenEstimator;
    }

    public void compact(
            ConversationContext context,
            long targetTokens
    ) throws Exception {

        List<Message> messages =
                context.getMessages();

        if (messages.isEmpty()) {
            return;
        }

        /*
         * Find the oldest messages that can be
         * compacted while keeping the recent
         * conversation within the target budget.
         */
        int splitIndex =
                findSplitIndex(
                        messages,
                        targetTokens
                );

        /*
         * Nothing can be compacted.
         */
        if (splitIndex <= 0) {
            return;
        }

        List<Message> messagesToCompact =
                new ArrayList<>(
                        messages.subList(
                                0,
                                splitIndex
                        )
                );

        /*
         * Generate a new summary from the
         * existing summary + old messages.
         */
        String newSummary =
                summarizer.summarize(
                        context.getSummary(),
                        messagesToCompact
                );

        context.setSummary(newSummary);

        /*
         * Keep only the recent messages.
         */
        List<Message> recentMessages =
                new ArrayList<>(
                        messages.subList(
                                splitIndex,
                                messages.size()
                        )
                );

        messages.clear();

        messages.addAll(recentMessages);
    }

    private int findSplitIndex(
            List<Message> messages,
            long targetTokens
    ) throws Exception {

        /*
         * Start with the assumption that we keep
         * the entire conversation.
         */
        int splitIndex = 0;

        /*
         * Gradually remove older messages until
         * the remaining conversation fits within
         * the target token budget.
         */
        for (int i = 1; i < messages.size(); i++) {

            List<Message> recentMessages =
                    messages.subList(
                            i,
                            messages.size()
                    );

            long estimatedTokens =
                    tokenEstimator.estimate(
                            "",
                            recentMessages
                    );

            if (estimatedTokens <= targetTokens) {
                splitIndex = i;
                break;
            }
        }

        return splitIndex;
    }
}