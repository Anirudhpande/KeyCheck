import javax.crypto.SecretKey;
import java.util.Map;

public class API {
    private final String provider_name;
    private final String url;
    private final String authHeader;
    private final String authPrefix;
    private final String body;
    private final Map<String, String> extraHeaders;
    private final String API;
    private final SecretKey secretKey;

    public API(
        String provider_name,
        String url,
        String authHeader,
        String authPrefix,
        String body,
        Map<String, String> extraHeaders,
        String API,
        SecretKey secretKey
    ) {
        this.provider_name = provider_name;
        this.url = url;
        this.authHeader = authHeader;
        this.authPrefix = authPrefix;
        this.body = body;
        this.extraHeaders = extraHeaders;
        this.API = API;
        this.secretKey = secretKey;
    }

    public String getProvider_name() {
        return provider_name;
    }

    public String getUrl() {
        return url;
    }

    public String getAuthHeader() {
        return authHeader;
    }

    public String getAuthPrefix() {
        return authPrefix;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getExtraHeaders() {
        return extraHeaders;
    }

    public String getAPI(){return API;}

    public SecretKey getSecretKey(){return secretKey;}
}