import java.util.List;
import java.util.Scanner;

public class ChatService {

    private final Provider provider;
    private final API api;
    private final Scanner scanner;

    public ChatService(Provider provider, API api, Scanner scanner){
        this.provider = provider;
        this.api = api;
        this.scanner = scanner;
    }

    public void Start() throws Exception {
        List<String> models = provider.getModels(api);

        String selectModel = ModelSelection.selectModel(models, scanner);

        while (true){

            System.out.println("\nYou");
            String prompt = scanner.nextLine();

            if(prompt.equalsIgnoreCase("exit")){
                break;
            }

            String answer = provider.sendRequest(api, selectModel, prompt);

            System.out.println("\nAI " + answer);
        }
    }
}
