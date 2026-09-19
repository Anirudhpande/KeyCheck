import javax.crypto.SecretKey;
import java.util.Map;

public class API {

    private final String provider_name;
    private final String authHeader;
    private final String authPrefix;
    private final Map<String, String> extraHeaders;
    private final String API;
    private final SecretKey secretKey;

    public API(
            String provider_name,
            String authHeader,
            String authPrefix,
            Map<String, String> extraHeaders,
            String API,
            SecretKey secretKey
    ) {
        this.provider_name = provider_name;
        this.authHeader = authHeader;
        this.authPrefix = authPrefix;
        this.extraHeaders = extraHeaders;
        this.API = API;
        this.secretKey = secretKey;
    }

    public String getProvider_name() {
        return provider_name;
    }

    public String getAuthHeader() {
        return authHeader;
    }

    public String getAuthPrefix() {
        return authPrefix;
    }

    public Map<String, String> getExtraHeaders() {
        return extraHeaders;
    }

    public String getAPI() {
        return API;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }
}