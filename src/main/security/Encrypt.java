package src.main.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class Encrypt {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;

    public static SecretKey generateKey() throws Exception{
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();

    }

    public static String encrypt(String plainText, SecretKey key) throws Exception{
        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);;
        cipher.init(Cipher.ENCRYPT_MODE, key ,parameterSpec);

        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        byte[] encryptedMessage = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, encryptedMessage, 0, iv.length);
        System.arraycopy(cipherText, 0, encryptedMessage, iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(encryptedMessage);
    }

    public static String decrypt(String encryptedBase64, SecretKey key) throws Exception{
        byte[] encryptedMessage = Base64.getDecoder().decode(encryptedBase64);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        System.arraycopy(encryptedMessage, 0, iv, 0, iv.length);

        int cipherTextLenght = encryptedMessage.length - IV_LENGTH_BYTE;
        byte[] cipherText = new byte[cipherTextLenght];
        System.arraycopy(encryptedMessage, IV_LENGTH_BYTE, cipherText, 0, cipherTextLenght);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

        byte[] plainTextBytes = cipher.doFinal(cipherText);
        return new String(plainTextBytes, StandardCharsets.UTF_8);
    }

}


