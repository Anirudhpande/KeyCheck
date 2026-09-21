package src.main.app;

import src.main.models.API;
import src.main.models.ProviderConfig;
import src.main.providers.Provider;
import src.main.providers.ProviderRegistry;
import src.main.security.Encrypt;
import src.main.service.ChatService;
import src.main.service.KeyManager;

import java.util.Scanner;
import javax.crypto.SecretKey;

public class KeyCheck {

    public static void main(String[] args) throws Exception {

        KeyManager keyManager =
                new KeyManager(
                        Encrypt.generateKey()
                );

        Scanner scanner =
                new Scanner(System.in);

        System.out.println("Enter API Provider Name:");
        String providerName =
                scanner.nextLine();

        System.out.println("Please Enter your API KEY");
        String API_KEY =
                scanner.nextLine();

        String encryptedAPI =
                keyManager.encrypt(API_KEY);

        SecretKey secretKey =
                keyManager.getSecretKey();

        ProviderConfig config =
                ProviderRegistry.getConfig(providerName);

        API api =
                new API(
                        config.getProviderName(),
                        config.getAuthHeader(),
                        config.getAuthPrefix(),
                        config.getExtraHeaders(),
                        encryptedAPI,
                        secretKey
                );

        Provider provider =
                ProviderRegistry.getProvider(providerName);

        ChatService chatService =
                new ChatService(
                        provider,
                        api,
                        scanner
                );

        chatService.start();
    }
}