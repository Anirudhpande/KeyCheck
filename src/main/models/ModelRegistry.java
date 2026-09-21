package src.main.models;

import java.util.HashMap;
import java.util.Map;

public class ModelRegistry {

    private static final long DEFAULT_CONTEXT_TOKENS = 8192;
    private static final long DEFAULT_OUTPUT_TOKENS = 2048;

    private static final Map<String, ModelInfo> models =
            new HashMap<>();

    public static ModelInfo getModel(
            String provider,
            String modelId
    ) {

        String key =
                provider.toLowerCase()
                        + ":"
                        + modelId;

        ModelInfo model = models.get(key);

        if (model != null) {
            return model;
        }

        return new ModelInfo(
                provider,
                modelId,
                DEFAULT_CONTEXT_TOKENS,
                DEFAULT_OUTPUT_TOKENS
        );
    }

    public static void register(ModelInfo model) {

        String key =
                model.getProvider().toLowerCase()
                        + ":"
                        + model.getModelId();

        models.put(key, model);
    }
}