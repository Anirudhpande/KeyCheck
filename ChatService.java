import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

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

        List<Message> messages = new ArrayList<>();
        List<String> models = provider.getModels(api);

        String selectModel = ModelSelection.selectModel(models, scanner);

        while (true){

            System.out.println("\nYou: ");
            String prompt = scanner.nextLine();

            if(prompt.equalsIgnoreCase("exit")){
                break;
            }

            messages.add(new Message("user", prompt));

            String answer = provider.sendRequest(api, selectModel,messages);

            messages.add(new Message("assistant", answer));

            System.out.println("\nAI: " + answer);
        }
    }
}
