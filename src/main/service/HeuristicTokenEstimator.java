package src.main.service;

import src.main.models.Message;

import java.util.List;

public class HeuristicTokenEstimator implements TokenEstimator {

    @Override
    public long estimate(
            String model,
            List<Message> messages
    ) {

        long characters = 0;

        for (Message message : messages) {
            characters += message.getRole().length();
            characters += message.getContent().length();
        }

        return Math.max(1, characters / 4);
    }
}