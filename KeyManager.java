import javax.crypto.SecretKey;

public class KeyManager {

    private final SecretKey secretKey;

    public KeyManager(SecretKey secretKey){
        this.secretKey = secretKey;
    }

    public String encrypt(String apiKey) throws Exception{
        return Encrypt.encrypt(apiKey, secretKey);
    }

    public String decrypt(String encryptedAPI) throws Exception{
        return Encrypt.decrypt(encryptedAPI, secretKey);
    }

    public SecretKey getSecretKey(){
        return secretKey;
    }
}
