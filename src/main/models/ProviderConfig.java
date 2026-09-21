package src.main.models;
import java.util.Map;

public class ProviderConfig {

    private final String providerName;
    private final String authHeader;
    private final String authPrefix;
    private final Map<String, String> extraHeaders;

    public ProviderConfig(
            String providerName,
            String authHeader,
            String authPrefix,
            Map<String, String> extraHeaders
    ){
        this.providerName = providerName;
        this.authHeader = authHeader;
        this.authPrefix = authPrefix;
        this.extraHeaders = extraHeaders;
    }

    public String getProviderName(){
        return providerName;
    }

    public String getAuthHeader(){
        return authHeader;
    }

    public String getAuthPrefix(){
        return authPrefix;
    }

    public Map<String, String> getExtraHeaders(){
        return extraHeaders;
    }

}
