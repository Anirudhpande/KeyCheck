import java.util.Map;

public class API {
    private final String name;
    private final String url;
    private final String authHeader;
    private final String authPrefix;
    private final String body;
    private final Map<String, String> extraHeaders;

    public API(
        String name,
        String url,
        String authHeader,
        String authPrefix,
        String body,
        Map<String, String> extraHeaders
    ) {
        this.name = name;
        this.url = url;
        this.authHeader = authHeader;
        this.authPrefix = authPrefix;
        this.body = body;
        this.extraHeaders = extraHeaders;
    }

    public String getName() {
        return name;
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
}