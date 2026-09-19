import java.util.List;

public interface Provider {

    List<String> getModels(API api) throws Exception;

    String sendRequest(
            API api,
            String selectedModel,
            String prompt
    ) throws Exception;

}
