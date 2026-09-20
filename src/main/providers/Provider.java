package src.main.providers;

import src.main.models.API;
import src.main.models.Message;

import java.util.List;

public interface Provider {

    List<String> getModels(API api) throws Exception;

    String sendRequest(
            API api,
            String selectedModel,
            List<Message> messages
    ) throws Exception;

}
