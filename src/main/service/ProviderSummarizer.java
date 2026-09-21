package src.main.service;

import  src.main.models.API;
import src.main.models.Message;
import src.main.models.ProviderResponse;
import src.main.providers.Provider;

import java.lang.reflect.Member;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class ProviderSummarizer implements Summarizer {

    private final Provider provider;
    private final API api;
    private final String model;

    public ProviderSummarizer(
            Provider provider,
            API api,
            String model
    ){
        this.provider = provider;
        this.model = model;
        this.api = api;

    }

    @Override
    public String summarize(
            String existingSummary,
            List<Message> messages
    ) throws Exception{

        List<Message> prompt = new ArrayList<>();

        StringBuilder content = new StringBuilder();

        content.append("Summarize the following conversation History");

        content.append("Preserve important facts, decisionsn " + "technical details, user preferances, " + "and unresolved tasks");

        content.append("Be concise and do not invent information.\n\n");

        if(existingSummary != null && !existingSummary.isBlank()){
            content.append("Existing Summary");
            content.append(existingSummary);
            content.append("\n\n");
        }

        content.append("Conversation history:\n");

        for(Message message: messages){
            content.append(message.getRole());
            content.append(": ");
            content.append(message.getContent());
            content.append("\n");
        }

        prompt.add(new Message("user", content.toString()));

        ProviderResponse response = provider.sendRequest(api, model, prompt);

        return response.getContent();

    }
}
