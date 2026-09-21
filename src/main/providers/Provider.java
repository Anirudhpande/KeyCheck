package src.main.providers;

import src.main.models.API;
import src.main.models.Message;
import src.main.models.ModelInfo;
import src.main.models.ProviderResponse;

import java.util.List;

public interface Provider {

    List<String> getModels(API api) throws Exception;

    ProviderResponse sendRequest(
            API api,
            String selectedModel,
            List<Message> messages
    ) throws Exception;

    ModelInfo getModelInfo(
            API api,
            String modelId
    ) throws Exception;

}
