package src.main.service;

import src.main.models.ConversationContext;
import src.main.models.Message;

import java.util.ArrayList;
import java.util.List;

public class CompactionService {

    private final Summarizer summarizer;

    public CompactionService(Summarizer summarizer) {
        this.summarizer = summarizer;
    }

    public void compact(
            ConversationContext context,
            int messagesToKeep
    ) throws Exception {

        List<Message> messages =
                context.getMessages();

        if (messages.size() <= messagesToKeep) {
            return;
        }

        int compactUntil =
                messages.size() - messagesToKeep;

        List<Message> messagesToCompact =
                new ArrayList<>(
                        messages.subList(
                                0,
                                compactUntil
                        )
                );

        String newSummary =
                summarizer.summarize(
                        context.getSummary(),
                        messagesToCompact
                );

        context.setSummary(newSummary);

        List<Message> recentMessages =
                new ArrayList<>(
                        messages.subList(
                                compactUntil,
                                messages.size()
                        )
                );

        messages.clear();

        messages.addAll(recentMessages);
    }
}